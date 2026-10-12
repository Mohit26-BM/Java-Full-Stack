package com.company.jdbc.app;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import com.company.jdbc.dao.AccountDao;
import com.company.jdbc.dao.AccountDaoImpl;
import com.company.jdbc.dao.TransactionDao;
import com.company.jdbc.dao.TransactionDaoImpl;
import com.company.jdbc.exception.AccountInactiveException;
import com.company.jdbc.exception.AccountNotFoundException;
import com.company.jdbc.exception.DataAccessException;
import com.company.jdbc.exception.DuplicateAccountException;
import com.company.jdbc.exception.InsufficientBalanceException;
import com.company.jdbc.exception.InvalidInputException;
import com.company.jdbc.exception.InvalidTransferException;
import com.company.jdbc.exception.TransferFailedException;
import com.company.jdbc.model.AccountStatus;
import com.company.jdbc.model.AccountTransaction;
import com.company.jdbc.model.BankAccount;
import com.company.jdbc.service.AccountService;
import com.company.jdbc.service.AccountServiceImpl;
import com.company.jdbc.service.TransferService;
import com.company.jdbc.service.TransferServiceImpl;

public class BankTransferApp {

    private static final String LINE =
            "--------------------------------------------------------------------------";

    private static AccountService accountService;
    private static TransferService transferService;

    public static void main(String[] args) {

        /*
        Manual dependency injection: DAO -> Service.
        */
        AccountDao accountDao = new AccountDaoImpl();
        TransactionDao transactionDao = new TransactionDaoImpl();

        accountService = new AccountServiceImpl(accountDao, transactionDao);
        transferService = new TransferServiceImpl(accountDao, transactionDao);

        while (true) {

            showMenu();

            int choice = ConsoleInput.readPositiveInt("Enter your choice: ");

            if (choice == 9) {
                System.out.println("\nThank you for using the Bank Transfer System.");
                return;
            }

            try {

                handleChoice(choice);

            } catch (DataAccessException e) {

                System.out.println("\nDatabase Error: " + e.getMessage());

                if (e.getCause() != null) {
                    System.out.println("Cause: " + e.getCause().getMessage());
                }

            } catch (IllegalStateException e) {

                System.out.println("\nConfiguration Error: " + e.getMessage());
            }
        }
    }

    private static void showMenu() {

        System.out.println("\n========================================");
        System.out.println("        BANK ACCOUNT TRANSFER");
        System.out.println("========================================");
        System.out.println("1. Create Account");
        System.out.println("2. Balance Lookup");
        System.out.println("3. View All Accounts");
        System.out.println("4. Transfer Money");
        System.out.println("5. Rollback Demo (failure after debit)");
        System.out.println("6. Transaction History");
        System.out.println("7. Change Account Status");
        System.out.println("8. Concurrency Demo (SELECT ... FOR UPDATE)");
        System.out.println("9. Exit");
        System.out.println("========================================");
    }

    private static void handleChoice(int choice) {

        switch (choice) {
            case 1: createAccount();      break;
            case 2: balanceLookup();      break;
            case 3: viewAllAccounts();    break;
            case 4: transferMoney();      break;
            case 5: rollbackDemo();       break;
            case 6: transactionHistory(); break;
            case 7: changeStatus();       break;
            case 8: concurrencyDemo();    break;
            default:
                System.out.println("\nInvalid choice. Please select 1 to 9.");
        }
    }

    // =========================================================
    // ACCOUNTS
    // =========================================================

    private static void createAccount() {

        System.out.println("\n========== Create Account ==========");

        String number = ConsoleInput.readLine("Account number (e.g. ACC1007): ");
        String name = ConsoleInput.readLine("Customer name: ");
        BigDecimal opening = ConsoleInput.readMoney("Opening balance: Rs. ");

        try {

            long accountId = accountService.createAccount(number, name, opening);

            System.out.println("\nAccount created. Generated account_id: " + accountId);

            printAccountTable(Collections.singletonList(
                    accountService.getAccount(number)));

        } catch (InvalidInputException e) {

            System.out.println("\nValidation Error: " + e.getMessage());

        } catch (DuplicateAccountException | AccountNotFoundException e) {

            System.out.println("\nError: " + e.getMessage());
        }
    }

    private static void balanceLookup() {

        System.out.println("\n========== Balance Lookup ==========");

        String number = ConsoleInput.readLine("Account number: ");

        try {

            BankAccount account = accountService.getAccount(number);

            System.out.println("-------------------------");
            System.out.println("Account  : " + account.getAccountNumber());
            System.out.println("Customer : " + account.getCustomerName());
            System.out.println("Balance  : Rs. " + account.getBalance());
            System.out.println("Status   : " + account.getStatus());
            System.out.println("-------------------------");

        } catch (AccountNotFoundException e) {

            System.out.println("\n" + e.getMessage());
        }
    }

    private static void viewAllAccounts() {

        System.out.println("\n========== All Accounts ==========");

        printAccountTable(accountService.getAllAccounts());
    }

    private static void changeStatus() {

        System.out.println("\n========== Change Account Status ==========");

        String number = ConsoleInput.readLine("Account number: ");

        System.out.println("1. ACTIVE   2. INACTIVE   3. BLOCKED");
        int choice = ConsoleInput.readPositiveInt("New status: ");

        AccountStatus status;

        switch (choice) {
            case 1: status = AccountStatus.ACTIVE;   break;
            case 2: status = AccountStatus.INACTIVE; break;
            case 3: status = AccountStatus.BLOCKED;  break;
            default:
                System.out.println("Invalid status.");
                return;
        }

        try {

            accountService.changeStatus(number, status);

            System.out.println("\nStatus changed to " + status + ".");

        } catch (AccountNotFoundException e) {

            System.out.println("\n" + e.getMessage());
        }
    }

    // =========================================================
    // TRANSFERS
    // =========================================================

    private static void transferMoney() {

        System.out.println("\n========== Transfer Money ==========");

        String from = ConsoleInput.readLine("From account: ");
        String to = ConsoleInput.readLine("To account: ");
        BigDecimal amount = ConsoleInput.readMoney("Amount: Rs. ");

        BigDecimal[] before = readBalances(from, to);

        try {

            long transactionId = transferService.transfer(from, to, amount);

            System.out.println("\nTransfer SUCCESSFUL and COMMITTED.");
            System.out.println("Transaction ID: " + transactionId);

            printBeforeAfter(from, to, before, readBalances(from, to));

        } catch (InvalidTransferException e) {

            System.out.println("\nValidation Error: " + e.getMessage());

        } catch (AccountNotFoundException
                 | AccountInactiveException
                 | InsufficientBalanceException e) {

            System.out.println("\nTransfer REJECTED: " + e.getMessage());
            printBeforeAfter(from, to, before, readBalances(from, to));

        } catch (TransferFailedException e) {

            System.out.println("\n" + e.getMessage());
            printBeforeAfter(from, to, before, readBalances(from, to));
        }
    }

    /*
    Acceptance tests:
    - a failed transfer leaves both balances unchanged
    - history is only written on a successful commit
    */
    private static void rollbackDemo() {

        System.out.println("\n========== Rollback Demo ==========");
        System.out.println("The source account is DEBITED, then a failure is");
        System.out.println("thrown BEFORE the credit. Everything must roll back.");

        String from = ConsoleInput.readLine("\nFrom account (e.g. ACC1001): ");
        String to = ConsoleInput.readLine("To account (e.g. ACC1002): ");
        BigDecimal amount = ConsoleInput.readMoney("Amount: Rs. ");

        BigDecimal[] before = readBalances(from, to);
        int historyBefore = accountService.countTransactions();

        try {

            transferService.transferWithFailureAfterDebit(from, to, amount);

            System.out.println("\nUnexpected: transfer was not interrupted.");

        } catch (TransferFailedException e) {

            System.out.println("\n" + e.getMessage());

        } catch (InvalidTransferException
                 | AccountNotFoundException
                 | AccountInactiveException
                 | InsufficientBalanceException e) {

            System.out.println("\nRejected before the debit: " + e.getMessage());
            System.out.println("(Use two ACTIVE accounts and an affordable amount "
                    + "to see the rollback.)");
            return;
        }

        BigDecimal[] after = readBalances(from, to);
        int historyAfter = accountService.countTransactions();

        printBeforeAfter(from, to, before, after);

        System.out.println("History rows before: " + historyBefore
                + " | after: " + historyAfter);

        boolean unchanged =
                equal(before[0], after[0])
                && equal(before[1], after[1])
                && historyBefore == historyAfter;

        System.out.println(unchanged
                ? "[OK] Rollback worked: balances and history are unchanged."
                : "[PROBLEM] Data changed even though the transfer failed!");
    }

    private static void transactionHistory() {

        System.out.println("\n========== Transaction History ==========");

        String number = ConsoleInput.readLine(
                "Account number (Enter for all accounts): ");

        try {

            List<AccountTransaction> transactions = number.isEmpty()
                    ? accountService.getAllHistory()
                    : accountService.getHistory(number);

            if (transactions.isEmpty()) {
                System.out.println("No transactions found.");
                return;
            }

            System.out.println(LINE);
            System.out.println(String.format(
                    "%-5s %-12s %-12s %16s  %-16s %s",
                    "ID", "From", "To", "Amount", "Type", "Time"));
            System.out.println(LINE);

            for (AccountTransaction transaction : transactions) {
                System.out.println(transaction);
            }

            System.out.println(LINE);

        } catch (AccountNotFoundException e) {

            System.out.println("\n" + e.getMessage());
        }
    }

    private static void concurrencyDemo() {

        System.out.println("\n========== Concurrency Demo ==========");
        System.out.println("This creates 4 demo accounts (DN... and DL...) "
                + "and takes about 4 seconds.");

        if (!ConsoleInput.readYes("Continue? (y/n): ")) {
            return;
        }

        try {

            new ConcurrencyDemo(accountService, transferService).run();

        } catch (Exception e) {

            System.out.println("\nDemo failed: " + e.getMessage());
        }
    }

    // =========================================================
    // OUTPUT HELPERS
    // =========================================================

    /*
    Returns { fromBalance, toBalance }; null for an unknown account.
    */
    private static BigDecimal[] readBalances(String from, String to) {

        return new BigDecimal[] { balanceOrNull(from), balanceOrNull(to) };
    }

    private static BigDecimal balanceOrNull(String accountNumber) {

        try {
            return accountService.getAccount(accountNumber).getBalance();
        } catch (AccountNotFoundException e) {
            return null;
        }
    }

    private static void printBeforeAfter(String from,
                                         String to,
                                         BigDecimal[] before,
                                         BigDecimal[] after) {

        System.out.println("-------------------------");
        System.out.println(String.format("%-10s %-14s %16s %16s",
                "", "Account", "Before", "After"));
        System.out.println(String.format("%-10s %-14s %16s %16s",
                "From", from.toUpperCase(), money(before[0]), money(after[0])));
        System.out.println(String.format("%-10s %-14s %16s %16s",
                "To", to.toUpperCase(), money(before[1]), money(after[1])));
        System.out.println("-------------------------");
    }

    private static String money(BigDecimal value) {
        return value == null ? "(not found)" : "Rs. " + value;
    }

    private static boolean equal(BigDecimal a, BigDecimal b) {
        return a == null ? b == null : b != null && a.compareTo(b) == 0;
    }

    private static void printAccountTable(List<BankAccount> accounts) {

        System.out.println(LINE);
        System.out.println(String.format(
                "%-4s %-12s %-22s %16s  %s",
                "ID", "Account", "Customer", "Balance", "Status"));
        System.out.println(LINE);

        for (BankAccount account : accounts) {
            System.out.println(account);
        }

        System.out.println(LINE);
    }
}
