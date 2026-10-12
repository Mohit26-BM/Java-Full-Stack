package com.company.wallet.app;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;

import com.company.wallet.exception.DuplicateCustomerException;
import com.company.wallet.exception.InsufficientFundsException;
import com.company.wallet.exception.TransactionFailedException;
import com.company.wallet.exception.ValidationException;
import com.company.wallet.exception.WalletException;
import com.company.wallet.exception.WalletInactiveException;
import com.company.wallet.exception.WalletNotFoundException;
import com.company.wallet.model.BulkCredit;
import com.company.wallet.model.BulkCreditResult;
import com.company.wallet.model.Customer;
import com.company.wallet.model.MonthlySummary;
import com.company.wallet.model.ReconciliationRow;
import com.company.wallet.model.TransferResult;
import com.company.wallet.model.Wallet;
import com.company.wallet.model.WalletStatus;
import com.company.wallet.model.WalletTransaction;

/*
Runs every MANDATORY DEMONSTRATION automatically and prints
[PASS] / [FAIL] for each check. The console output is the
"test evidence" for success and failure cases.

It creates its own fresh customers and wallets on every run
(unique email/phone), so it never depends on - or breaks -
the sample data, and can be run as often as needed.
*/
public class DemoRunner {

    private final AppContext context;

    private int passed;
    private int failed;

    public DemoRunner(AppContext context) {
        this.context = context;
    }

    public void runAll() throws WalletException, InterruptedException {

        passed = 0;
        failed = 0;

        // ---------- setup ----------

        section("1. Customer registration + wallet creation (generated keys)");

        long idA = DemoData.next();
        long idB = DemoData.next();

        Customer alice = context.customers().register(
                "Demo Sender", DemoData.email("sender", idA), DemoData.phone(idA));
        Customer bob = context.customers().register(
                "Demo Receiver", DemoData.email("receiver", idB), DemoData.phone(idB));

        check("Customers registered with generated IDs",
                alice.getCustomerId() > 0 && bob.getCustomerId() > 0,
                "ids " + alice.getCustomerId() + ", " + bob.getCustomerId());

        Wallet walletA = context.wallets().createWallet(alice.getCustomerId(), money("5000"));
        Wallet walletB = context.wallets().createWallet(bob.getCustomerId(), money("1000"));

        String a = walletA.getWalletNumber();
        String b = walletB.getWalletNumber();

        check("Wallet numbers built from generated keys",
                a.equals(String.format("WAL%07d", walletA.getWalletId())),
                a + " (Rs. 5000), " + b + " (Rs. 1000)");

        // ---------- duplicate data ----------

        section("2. Duplicate-data handling");

        expect("Same email rejected", DuplicateCustomerException.class,
                () -> context.customers().register("Copy Cat", alice.getEmail(), DemoData.phone(DemoData.next())));

        expect("Same phone rejected", DuplicateCustomerException.class,
                () -> context.customers().register("Copy Cat", DemoData.email("copy", DemoData.next()), alice.getPhone()));

        // ---------- successful transfer ----------

        section("3. Successful transfer");

        TransferResult transfer = context.wallets().transfer(a, b, money("1500"), "Demo transfer");

        check("Balances after transfer", balance(a).equals(money("3500")) && balance(b).equals(money("2500")),
                a + " = " + balance(a) + ", " + b + " = " + balance(b));

        WalletTransaction out = context.wallets().history(a, 1).get(0);
        WalletTransaction in = context.wallets().history(b, 1).get(0);

        check("Two ledger rows share one reference",
                out.getReference().equals(transfer.getReference())
                        && in.getReference().equals(transfer.getReference()),
                transfer.getReference());

        // ---------- insufficient funds ----------

        section("4. Insufficient-funds rejection");

        expectUnchanged("Transfer more than the balance", InsufficientFundsException.class, a, b,
                () -> context.wallets().transfer(b, a, money("100000"), null));

        // ---------- invalid destination ----------

        section("5. Invalid destination rejection");

        expectUnchanged("Destination wallet does not exist", WalletNotFoundException.class, a, b,
                () -> context.wallets().transfer(a, "WAL9999999", money("100"), null));

        expectUnchanged("Destination is the same wallet", ValidationException.class, a, b,
                () -> context.wallets().transfer(a, a, money("100"), null));

        context.wallets().changeStatus(b, WalletStatus.BLOCKED);

        expectUnchanged("Destination wallet is BLOCKED", WalletInactiveException.class, a, b,
                () -> context.wallets().transfer(a, b, money("100"), null));

        context.wallets().changeStatus(b, WalletStatus.ACTIVE);

        // ---------- forced failure + rollback ----------

        section("6. Forced failure followed by rollback");

        long ledgerBefore = context.reports().countLedgerRows();

        expectUnchanged("Failure after the debit", TransactionFailedException.class, a, b,
                () -> context.wallets().transferWithForcedFailure(a, b, money("500")));

        long ledgerAfter = context.reports().countLedgerRows();

        check("No ledger rows were kept", ledgerBefore == ledgerAfter,
                "ledger rows " + ledgerBefore + " -> " + ledgerAfter);

        // ---------- batch ----------

        section("7. Batch operation (bulk credit)");

        List<BulkCredit> credits = Arrays.asList(
                new BulkCredit(1, a, money("100"), "Demo cashback"),
                new BulkCredit(2, b, money("200"), "Demo cashback"),
                new BulkCredit(3, a, money("50"), "Demo referral bonus"));

        BulkCreditResult batch = context.wallets().bulkCredit(credits);

        System.out.println("   balance UPDATE batch counts: " + Arrays.toString(batch.getBalanceUpdateCounts()));
        System.out.println("   ledger INSERT batch counts : " + Arrays.toString(batch.getLedgerInsertCounts()));

        check("Batch applied", balance(a).equals(money("3650")) && balance(b).equals(money("2700")),
                a + " = " + balance(a) + ", " + b + " = " + balance(b));

        expectUnchanged("Batch with an unknown wallet is all-or-nothing", WalletNotFoundException.class, a, b,
                () -> context.wallets().bulkCredit(Arrays.asList(
                        new BulkCredit(1, a, money("10"), null),
                        new BulkCredit(2, "WAL9999998", money("10"), null))));

        // ---------- JOIN report ----------

        section("8. JOIN-based report (monthly report for " + YearMonth.now() + ")");

        List<MonthlySummary> report = context.reports().monthlyReport(YearMonth.now());
        Printer.monthlyReport(report);

        MonthlySummary rowA = find(report, a);
        MonthlySummary rowB = find(report, b);

        check("Report shows customer names (JOIN)",
                rowA != null && rowB != null && "Demo Sender".equals(rowA.getCustomerName()),
                rowA == null ? "row missing" : rowA.getCustomerName());

        check("Report net change = wallet balance (all activity this month)",
                rowA != null && rowB != null
                        && rowA.getNetChange().compareTo(balance(a)) == 0
                        && rowB.getNetChange().compareTo(balance(b)) == 0,
                rowA == null ? "-" : a + " net " + rowA.getNetChange() + ", " + b + " net "
                        + (rowB == null ? "-" : rowB.getNetChange()));

        check("Stored balance = ledger balance (reconciliation)",
                reconciles(a) && reconciles(b), a + ", " + b);

        // ---------- concurrency ----------

        section("9. Concurrency experiment");

        boolean lockedCorrect = new ConcurrencyExperiment(context).run();

        check("FOR UPDATE prevents the lost update", lockedCorrect, "see output above");

        // ---------- resources ----------

        section("10. Resource handling");

        check("All pooled connections returned", context.activeConnections() == 0,
                "active connections = " + context.activeConnections());

        // ---------- summary ----------

        System.out.println("\n========================================");
        System.out.println(" RESULT: " + passed + " passed, " + failed + " failed");
        System.out.println("========================================");
    }

    // =========================================================
    // CHECK HELPERS
    // =========================================================

    @FunctionalInterface
    private interface Action {
        void run() throws Exception;
    }

    private void section(String title) {
        System.out.println("\n>> " + title);
    }

    private void check(String name, boolean ok, String detail) {

        if (ok) {
            passed++;
        } else {
            failed++;
        }

        System.out.println((ok ? "[PASS] " : "[FAIL] ") + name + "  (" + detail + ")");
    }

    /*
    The action must throw exactly the expected exception type.
    */
    private void expect(String name, Class<? extends Exception> expected, Action action) {

        try {

            action.run();
            check(name, false, "no exception was thrown");

        } catch (Exception e) {

            check(name, expected.isInstance(e), ErrorHandler.label(e));
        }
    }

    /*
    Like expect(), and also verifies both wallet balances did not change.
    */
    private void expectUnchanged(String name,
                                 Class<? extends Exception> expected,
                                 String walletA,
                                 String walletB,
                                 Action action) throws WalletException {

        BigDecimal beforeA = balance(walletA);
        BigDecimal beforeB = balance(walletB);

        expect(name, expected, action);

        boolean unchanged = beforeA.equals(balance(walletA)) && beforeB.equals(balance(walletB));

        check("   balances unchanged", unchanged,
                walletA + " " + beforeA + " -> " + balance(walletA) + ", "
                        + walletB + " " + beforeB + " -> " + balance(walletB));
    }

    private BigDecimal balance(String walletNumber) throws WalletException {
        return context.wallets().getWallet(walletNumber).getBalance();
    }

    private boolean reconciles(String walletNumber) {

        for (ReconciliationRow row : context.reports().reconciliation()) {

            if (row.getWalletNumber().equals(walletNumber)) {
                return row.isMatching();
            }
        }

        return false;
    }

    private MonthlySummary find(List<MonthlySummary> rows, String walletNumber) {

        for (MonthlySummary row : rows) {

            if (walletNumber.equals(row.getWalletNumber())) {
                return row;
            }
        }

        return null;
    }

    private static BigDecimal money(String value) {
        return new BigDecimal(value).setScale(2);
    }
}
