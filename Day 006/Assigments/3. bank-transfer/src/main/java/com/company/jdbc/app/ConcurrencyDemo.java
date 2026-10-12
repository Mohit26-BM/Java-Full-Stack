package com.company.jdbc.app;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;

import com.company.jdbc.exception.AccountNotFoundException;
import com.company.jdbc.service.AccountService;
import com.company.jdbc.service.TransferService;

/*
OPTIONAL CHALLENGE: SELECT ... FOR UPDATE

THE PROBLEM (a "race condition")
--------------------------------
Account X has Rs. 1000. Two transfers of Rs. 800 start at the same time.

    Thread 1: reads balance = 1000 -> 1000 >= 800, OK
    Thread 2: reads balance = 1000 -> 1000 >= 800, OK   (thread 1 has not debited yet)
    Thread 1: debits 800  -> balance 200, commits
    Thread 2: debits 800  -> balance -600, commits      <- overdrawn!

Both threads checked an OLD value. The check ("is there enough money?")
and the update ("take the money") are separate steps, and another
transaction slipped in between them.

THE FIX
-------
SELECT ... FOR UPDATE reads the row AND locks it until commit/rollback.

    Thread 1: SELECT ... FOR UPDATE -> 1000, row LOCKED
    Thread 2: SELECT ... FOR UPDATE -> WAITS for thread 1's lock
    Thread 1: debits 800 -> 200, COMMIT, lock released
    Thread 2: now reads the CURRENT value 200 -> 200 < 800 -> rejected

The balance can never go negative, and no update is lost.

This demo creates two fresh accounts for each run (so your real data
is not touched), runs both transfers in parallel threads, and prints
the final balance - first WITHOUT the lock, then WITH it.
*/
public class ConcurrencyDemo {

    private static final BigDecimal OPENING_BALANCE = new BigDecimal("1000.00");
    private static final BigDecimal TRANSFER_AMOUNT = new BigDecimal("800.00");

    /*
    Each thread waits this long between reading the balance and
    debiting it, so the two threads overlap every time.
    */
    private static final long PAUSE_MILLIS = 1500;

    private final AccountService accountService;
    private final TransferService transferService;

    public ConcurrencyDemo(AccountService accountService,
                           TransferService transferService) {

        this.accountService = accountService;
        this.transferService = transferService;
    }

    public void run() throws Exception {

        System.out.println("\nScenario: source has Rs. " + OPENING_BALANCE
                + ". Two threads each transfer Rs. " + TRANSFER_AMOUNT
                + " at the same time.");
        System.out.println("Only ONE of them should succeed.");

        runScenario(false);
        runScenario(true);
    }

    private void runScenario(boolean useRowLock) throws Exception {

        System.out.println("\n========================================");
        System.out.println(useRowLock
                ? " WITH  SELECT ... FOR UPDATE"
                : " WITHOUT row lock (plain SELECT)");
        System.out.println("========================================");

        /*
        Unique demo account numbers, e.g. DN12345678S / DN12345678R
        */
        String suffix = String.valueOf(System.currentTimeMillis()).substring(5);
        String prefix = useRowLock ? "DL" : "DN";

        String source = prefix + suffix + "S";
        String target = prefix + suffix + "R";

        accountService.createAccount(source, "Demo Source", OPENING_BALANCE);
        accountService.createAccount(target, "Demo Receiver", BigDecimal.ZERO);

        System.out.println("Created " + source + " (Rs. " + OPENING_BALANCE
                + ") and " + target + " (Rs. 0.00)");

        String[] results = new String[2];

        /*
        The latch makes both threads start at the same moment.
        */
        CountDownLatch startSignal = new CountDownLatch(1);

        Thread first = new Thread(
                transferTask(source, target, useRowLock, startSignal, results, 0),
                "Thread-1");

        Thread second = new Thread(
                transferTask(source, target, useRowLock, startSignal, results, 1),
                "Thread-2");

        first.start();
        second.start();

        startSignal.countDown();

        first.join();
        second.join();

        System.out.println("Thread-1: " + results[0]);
        System.out.println("Thread-2: " + results[1]);

        printFinalBalance(source, target);
    }

    private Runnable transferTask(String source,
                                  String target,
                                  boolean useRowLock,
                                  CountDownLatch startSignal,
                                  String[] results,
                                  int index) {

        return () -> {

            try {

                startSignal.await();

                long transactionId = transferService.transferForConcurrencyDemo(
                        source, target, TRANSFER_AMOUNT,
                        useRowLock, PAUSE_MILLIS);

                results[index] = "SUCCESS (transaction " + transactionId + ")";

            } catch (Exception e) {

                results[index] = "REJECTED - " + e.getMessage();
            }
        };
    }

    private void printFinalBalance(String source, String target)
            throws AccountNotFoundException {

        BigDecimal sourceBalance =
                accountService.getAccount(source).getBalance();

        BigDecimal targetBalance =
                accountService.getAccount(target).getBalance();

        System.out.println("-------------------------");
        System.out.println("Final " + source + " balance: Rs. " + sourceBalance);
        System.out.println("Final " + target + " balance: Rs. " + targetBalance);

        if (sourceBalance.signum() < 0) {
            System.out.println("RESULT: PROBLEM - the account was overdrawn. "
                    + "Both threads passed the balance check using a stale value.");
        } else {
            System.out.println("RESULT: CORRECT - only one transfer went through; "
                    + "balance never went negative.");
        }
    }
}
