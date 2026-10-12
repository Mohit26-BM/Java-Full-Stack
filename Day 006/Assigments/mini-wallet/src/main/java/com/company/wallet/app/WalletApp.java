package com.company.wallet.app;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

import com.company.wallet.exception.ValidationException;
import com.company.wallet.exception.WalletException;
import com.company.wallet.model.BulkCredit;
import com.company.wallet.model.BulkCreditResult;
import com.company.wallet.model.Customer;
import com.company.wallet.model.TransferResult;
import com.company.wallet.model.Wallet;
import com.company.wallet.model.WalletStatus;
import com.company.wallet.model.WalletTransaction;
import com.company.wallet.service.BulkCreditFileReader;

/*
Console entry point.

Menu actions contain NO try/catch: every exception reaches the loop
in runMenu() and is reported by ErrorHandler (centralized handling).
*/
public class WalletApp {

    private static AppContext context;

    public static void main(String[] args) {

        try (AppContext appContext = startContext()) {

            if (appContext == null) {
                return;
            }

            context = appContext;
            runMenu();
        }

        System.out.println("Connection pool closed. Goodbye.");
    }

    private static AppContext startContext() {

        try {

            AppContext appContext = new AppContext();
            System.out.println("Connected. Mini Wallet Platform is ready.");
            return appContext;

        } catch (RuntimeException e) {

            System.out.println("Could not connect to the database: " + e.getMessage());
            System.out.println("Run schema.sql and sample-data.sql, start MySQL, and check db.properties.");
            return null;
        }
    }

    private static void runMenu() {

        while (true) {

            showMenu();

            int choice = ConsoleInput.readPositiveInt("Enter your choice: ");

            if (choice == 20) {
                return;
            }

            try {

                handleChoice(choice);

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
                System.out.println("Interrupted.");

            } catch (Exception e) {

                ErrorHandler.handle(e);
            }
        }
    }

    private static void showMenu() {

        System.out.println("\n==============================================");
        System.out.println("          MINI WALLET PLATFORM");
        System.out.println("==============================================");
        System.out.println(" CUSTOMERS                 MONEY");
        System.out.println("  1. Register customer      8. Deposit");
        System.out.println("  2. Find customer          9. Withdraw");
        System.out.println("  3. List customers        10. Transfer");
        System.out.println(" WALLETS                   11. Bulk credit (batch CSV)");
        System.out.println("  4. Create wallet         REPORTS");
        System.out.println("  5. Wallet details        12. Transaction history");
        System.out.println("  6. List wallets          13. Wallet monthly summary");
        System.out.println("  7. Change wallet status  14. Monthly report - all wallets (JOIN)");
        System.out.println("                           15. Customer portfolio (JOIN)");
        System.out.println(" DEMONSTRATIONS            16. Reconciliation (balance vs ledger)");
        System.out.println(" 17. Run ALL mandatory demonstrations (test evidence)");
        System.out.println(" 18. Forced-failure transfer (rollback)");
        System.out.println(" 19. Concurrency experiment");
        System.out.println(" 20. Exit");
        System.out.println("==============================================");
    }

    private static void handleChoice(int choice) throws Exception {

        switch (choice) {
            case 1:  registerCustomer();     break;
            case 2:  findCustomer();         break;
            case 3:  Printer.customers(context.customers().findAll()); break;
            case 4:  createWallet();         break;
            case 5:  walletDetails();        break;
            case 6:  Printer.wallets(context.wallets().getAllWallets()); break;
            case 7:  changeStatus();         break;
            case 8:  deposit();              break;
            case 9:  withdraw();             break;
            case 10: transfer();             break;
            case 11: bulkCredit();           break;
            case 12: history();              break;
            case 13: walletMonthlySummary(); break;
            case 14: monthlyReport();        break;
            case 15: Printer.portfolio(context.reports().customerPortfolio()); break;
            case 16: Printer.reconciliation(context.reports().reconciliation()); break;
            case 17: new DemoRunner(context).runAll(); break;
            case 18: forcedFailure();        break;
            case 19: new ConcurrencyExperiment(context).run(); break;
            default:
                System.out.println("\nInvalid choice. Please select 1 to 20.");
        }
    }

    // =========================================================
    // CUSTOMERS
    // =========================================================

    private static void registerCustomer() throws WalletException {

        System.out.println("\n--- Register Customer ---");

        String name = ConsoleInput.readLine("Full name: ");
        String email = ConsoleInput.readLine("Email: ");
        String phone = ConsoleInput.readLine("Phone (10 digits): ");

        Customer customer = context.customers().register(name, email, phone);

        System.out.println("\nCustomer registered. Customer ID: " + customer.getCustomerId());
    }

    private static void findCustomer() throws WalletException {

        System.out.println("\n--- Find Customer ---");

        String input = ConsoleInput.readLine("Customer ID or email: ");

        Customer customer = input.matches("\\d+")
                ? context.customers().findById(Long.parseLong(input))
                : context.customers().findByEmail(input);

        Printer.customers(Arrays.asList(customer));

        System.out.println("Wallets:");
        Printer.wallets(context.customers().walletsOf(customer.getCustomerId()));
    }

    // =========================================================
    // WALLETS
    // =========================================================

    private static void createWallet() throws WalletException {

        System.out.println("\n--- Create Wallet ---");

        long customerId = ConsoleInput.readPositiveLong("Customer ID: ");
        BigDecimal opening = ConsoleInput.readMoney("Opening deposit (0 for none): Rs. ");

        Wallet wallet = context.wallets().createWallet(customerId, opening);

        System.out.println("\nWallet created: " + wallet.getWalletNumber()
                + " (generated wallet_id " + wallet.getWalletId() + ")");
        Printer.wallets(Arrays.asList(wallet));
    }

    private static void walletDetails() throws WalletException {

        String number = ConsoleInput.readLine("\nWallet number (e.g. WAL0000001): ");

        Printer.wallets(Arrays.asList(context.wallets().getWallet(number)));
    }

    private static void changeStatus() throws WalletException {

        System.out.println("\n--- Change Wallet Status ---");

        String number = ConsoleInput.readLine("Wallet number: ");
        String status = ConsoleInput.readLine("New status (ACTIVE / INACTIVE / BLOCKED): ").toUpperCase();

        WalletStatus newStatus;

        try {
            newStatus = WalletStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Status must be ACTIVE, INACTIVE or BLOCKED.");
        }

        context.wallets().changeStatus(number, newStatus);

        System.out.println("\nWallet " + number.toUpperCase() + " is now " + newStatus + ".");
    }

    // =========================================================
    // MONEY
    // =========================================================

    private static void deposit() throws WalletException {

        System.out.println("\n--- Deposit ---");

        String number = ConsoleInput.readLine("Wallet number: ");
        BigDecimal amount = ConsoleInput.readMoney("Amount: Rs. ");
        String note = ConsoleInput.readLine("Description (optional): ");

        WalletTransaction transaction = context.wallets().deposit(number, amount, note);

        System.out.println("\nDeposit successful. New balance: " + Printer.money(transaction.getBalanceAfter())
                + " (transaction " + transaction.getTransactionId() + ")");
    }

    private static void withdraw() throws WalletException {

        System.out.println("\n--- Withdraw ---");

        String number = ConsoleInput.readLine("Wallet number: ");
        BigDecimal amount = ConsoleInput.readMoney("Amount: Rs. ");
        String note = ConsoleInput.readLine("Description (optional): ");

        WalletTransaction transaction = context.wallets().withdraw(number, amount, note);

        System.out.println("\nWithdrawal successful. New balance: " + Printer.money(transaction.getBalanceAfter())
                + " (transaction " + transaction.getTransactionId() + ")");
    }

    private static void transfer() throws WalletException {

        System.out.println("\n--- Transfer ---");

        String from = ConsoleInput.readLine("From wallet: ");
        String to = ConsoleInput.readLine("To wallet: ");
        BigDecimal amount = ConsoleInput.readMoney("Amount: Rs. ");
        String note = ConsoleInput.readLine("Description (optional): ");

        TransferResult result = context.wallets().transfer(from, to, amount, note);

        System.out.println("\nTransfer successful and COMMITTED.");
        System.out.println("Reference          : " + result.getReference());
        System.out.println("Source balance     : " + Printer.money(result.getSourceBalanceAfter()));
        System.out.println("Destination balance: " + Printer.money(result.getDestinationBalanceAfter()));
    }

    private static void bulkCredit() throws WalletException {

        System.out.println("\n--- Bulk Credit (batch) ---");
        System.out.println("CSV format: wallet_number,amount,description");

        String path = ConsoleInput.readLine("CSV path (Enter for bundled bulk-credits.csv): ");

        List<BulkCredit> credits = BulkCreditFileReader.read(path.isEmpty() ? null : path);

        System.out.println(credits.size() + " line(s) read. Total wallets affected will be shown after the batch.");

        BulkCreditResult result = context.wallets().bulkCredit(credits);

        System.out.println("\nBulk credit COMMITTED.");
        System.out.println("Lines credited          : " + result.getLineCount());
        System.out.println("Wallets updated         : " + result.getWalletCount());
        System.out.println("Total credited          : " + Printer.money(result.getTotalAmount()));
        System.out.println("Balance UPDATE batch    : " + Arrays.toString(result.getBalanceUpdateCounts()));
        System.out.println("Ledger INSERT batch     : " + Arrays.toString(result.getLedgerInsertCounts()));
    }

    // =========================================================
    // REPORTS
    // =========================================================

    private static void history() throws WalletException {

        String number = ConsoleInput.readLine("\nWallet number: ");
        int limit = ConsoleInput.readPositiveInt("How many (Enter for 20): ", 20);

        Wallet wallet = context.wallets().getWallet(number);

        System.out.println("\nHistory of " + wallet.getWalletNumber() + " (" + wallet.getCustomerName()
                + "), current balance " + Printer.money(wallet.getBalance()));

        Printer.history(context.wallets().history(number, limit));
    }

    private static void walletMonthlySummary() throws WalletException {

        String number = ConsoleInput.readLine("\nWallet number: ");

        Printer.walletMonthly(context.reports().walletMonthlySummary(number));
    }

    private static void monthlyReport() throws WalletException {

        String input = ConsoleInput.readLine("\nMonth (yyyy-MM, Enter for current month): ");

        YearMonth month;

        try {
            month = input.isEmpty() ? YearMonth.now() : YearMonth.parse(input);
        } catch (DateTimeParseException e) {
            throw new ValidationException("Month must look like 2026-09.");
        }

        System.out.println("\nMonthly report for " + month);

        Printer.monthlyReport(context.reports().monthlyReport(month));
    }

    // =========================================================
    // DEMONSTRATIONS
    // =========================================================

    private static void forcedFailure() throws WalletException {

        System.out.println("\n--- Forced-failure transfer ---");
        System.out.println("The source is debited and its ledger row written, then a failure");
        System.out.println("is forced BEFORE the credit. Everything must be rolled back.");

        String from = ConsoleInput.readLine("From wallet (e.g. WAL0000001): ");
        String to = ConsoleInput.readLine("To wallet (e.g. WAL0000002): ");
        BigDecimal amount = ConsoleInput.readMoney("Amount: Rs. ");

        BigDecimal fromBefore = context.wallets().getWallet(from).getBalance();
        BigDecimal toBefore = context.wallets().getWallet(to).getBalance();
        long ledgerBefore = context.reports().countLedgerRows();

        try {

            context.wallets().transferWithForcedFailure(from, to, amount);

        } catch (WalletException e) {

            ErrorHandler.handle(e);
        }

        BigDecimal fromAfter = context.wallets().getWallet(from).getBalance();
        BigDecimal toAfter = context.wallets().getWallet(to).getBalance();
        long ledgerAfter = context.reports().countLedgerRows();

        System.out.println("\nSource      : " + fromBefore + " -> " + fromAfter);
        System.out.println("Destination : " + toBefore + " -> " + toAfter);
        System.out.println("Ledger rows : " + ledgerBefore + " -> " + ledgerAfter);

        boolean unchanged = fromBefore.equals(fromAfter) && toBefore.equals(toAfter) && ledgerBefore == ledgerAfter;

        System.out.println(unchanged
                ? "[OK] Rollback restored the database state."
                : "[PROBLEM] Data changed after a failed transfer!");
    }
}
