package com.company.wallet.app;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;

import com.company.wallet.exception.WalletException;
import com.company.wallet.model.Customer;
import com.company.wallet.model.ReconciliationRow;
import com.company.wallet.model.Wallet;

/*
DOCUMENTED CONCURRENCY EXPERIMENT (also explained in README.md)

Question: what happens when two requests update the same row at once?

Setup: a fresh wallet with Rs. 1000. Two threads each withdraw Rs. 800
at the same moment. Each thread waits 1.5 s between READING the balance
and WRITING the new balance, so their work overlaps every time.

Run 1 - WITHOUT row lock (plain SELECT):
    T1 reads 1000 -> 1000 >= 800 OK -> will write 200
    T2 reads 1000 -> 1000 >= 800 OK -> will write 200
    T1 writes balance = 200, commits
    T2 writes balance = 200, commits   <- T1's update is LOST
  Result: Rs. 1600 withdrawn (two ledger rows), balance still 200.
  The reconciliation check catches it: balance 200 vs ledger -600.

Run 2 - WITH SELECT ... FOR UPDATE:
    T1 reads 1000 and LOCKS the row
    T2 tries to read the row -> WAITS for T1's lock
    T1 writes 200, commits, lock released
    T2 now reads 200 -> 200 < 800 -> InsufficientFundsException
  Result: one withdrawal, balance 200, ledger matches.
*/
public class ConcurrencyExperiment {

    private static final BigDecimal OPENING = new BigDecimal("1000.00");
    private static final BigDecimal WITHDRAWAL = new BigDecimal("800.00");
    private static final long PAUSE_MILLIS = 1500;

    private final AppContext context;

    public ConcurrencyExperiment(AppContext context) {
        this.context = context;
    }

    /*
    Returns true when the locked run behaved correctly.
    */
    public boolean run() throws WalletException, InterruptedException {

        System.out.println("Wallet starts with Rs. " + OPENING + "; two threads each withdraw Rs. "
                + WITHDRAWAL + " at the same time.");

        boolean unlockedConsistent = runScenario(false);
        boolean lockedConsistent = runScenario(true);

        System.out.println("\nConclusion:");
        System.out.println(" - Without a lock: "
                + (unlockedConsistent ? "no anomaly this time (timing-dependent)"
                                      : "LOST UPDATE - money left the wallet but the balance did not drop"));
        System.out.println(" - With FOR UPDATE: "
                + (lockedConsistent ? "correct - the second withdrawal saw the real balance and was rejected"
                                    : "unexpected inconsistency"));

        return lockedConsistent;
    }

    private boolean runScenario(boolean useRowLock) throws WalletException, InterruptedException {

        System.out.println("\n--- " + (useRowLock ? "WITH SELECT ... FOR UPDATE" : "WITHOUT row lock") + " ---");

        long id = DemoData.next();

        Customer customer = context.customers().register(
                useRowLock ? "Concurrency Locked" : "Concurrency Unlocked",
                DemoData.email("concurrency", id),
                DemoData.phone(id));

        Wallet wallet = context.wallets().createWallet(customer.getCustomerId(), OPENING);
        String number = wallet.getWalletNumber();

        System.out.println("Created " + number + " with Rs. " + OPENING);

        String[] results = new String[2];
        CountDownLatch start = new CountDownLatch(1);

        Thread first = new Thread(task(number, useRowLock, start, results, 0), "Withdraw-1");
        Thread second = new Thread(task(number, useRowLock, start, results, 1), "Withdraw-2");

        first.start();
        second.start();
        start.countDown();
        first.join();
        second.join();

        System.out.println("Withdraw-1: " + results[0]);
        System.out.println("Withdraw-2: " + results[1]);

        ReconciliationRow row = findReconciliation(number);

        System.out.println("Stored balance : " + Printer.money(row.getStoredBalance()));
        System.out.println("Ledger balance : " + Printer.money(row.getLedgerBalance()));
        System.out.println(row.isMatching() ? "Consistent." : "INCONSISTENT - lost update detected.");

        return row.isMatching();
    }

    private Runnable task(String walletNumber,
                          boolean useRowLock,
                          CountDownLatch start,
                          String[] results,
                          int index) {

        return () -> {

            try {

                start.await();

                context.wallets().withdrawForExperiment(walletNumber, WITHDRAWAL, useRowLock, PAUSE_MILLIS);

                results[index] = "SUCCESS";

            } catch (Exception e) {

                results[index] = "REJECTED - " + ErrorHandler.label(e);
            }
        };
    }

    private ReconciliationRow findReconciliation(String walletNumber) {

        for (ReconciliationRow row : context.reports().reconciliation()) {

            if (row.getWalletNumber().equals(walletNumber)) {
                return row;
            }
        }

        throw new IllegalStateException("Wallet " + walletNumber + " missing from reconciliation");
    }
}
