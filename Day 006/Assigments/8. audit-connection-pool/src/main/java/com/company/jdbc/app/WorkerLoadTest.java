package com.company.jdbc.app;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.company.jdbc.config.ConnectionPool;
import com.company.jdbc.config.PoolStats;
import com.company.jdbc.exception.PoolExhaustedException;
import com.company.jdbc.model.AuditEvent;
import com.company.jdbc.service.AuditService;

/*
Runs repeated inserts and selects from many worker threads that all
share the SAME small pool (5 connections).

With more threads than connections, some threads must wait for a
connection to be returned. A monitor samples the pool every 20 ms
to prove that "active" never goes above the pool size, and the
final snapshot proves every borrowed connection was returned.
*/
public class WorkerLoadTest {

    private final AuditService auditService;
    private final ConnectionPool pool;

    private final AtomicInteger inserts = new AtomicInteger();
    private final AtomicInteger selects = new AtomicInteger();
    private final AtomicInteger poolTimeouts = new AtomicInteger();
    private final AtomicInteger otherErrors = new AtomicInteger();

    private final AtomicInteger maxActive = new AtomicInteger();
    private final AtomicInteger maxWaiting = new AtomicInteger();

    public WorkerLoadTest(AuditService auditService, ConnectionPool pool) {
        this.auditService = auditService;
        this.pool = pool;
    }

    public void run(int threadCount, int operationsPerThread) throws InterruptedException {

        System.out.println("\nPool before : " + pool.getStats());
        System.out.println("Starting " + threadCount + " worker threads x "
                + operationsPerThread + " operations (insert + select each)...\n");

        ScheduledExecutorService monitor = Executors.newSingleThreadScheduledExecutor();
        monitor.scheduleAtFixedRate(this::sample, 0, 20, TimeUnit.MILLISECONDS);

        ExecutorService workers = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startSignal = new CountDownLatch(1);

        long start = System.nanoTime();

        for (int i = 1; i <= threadCount; i++) {

            final int workerNumber = i;

            workers.submit(() -> {
                awaitQuietly(startSignal);
                work(workerNumber, operationsPerThread);
            });
        }

        startSignal.countDown();

        workers.shutdown();
        workers.awaitTermination(5, TimeUnit.MINUTES);

        long elapsedMillis = (System.nanoTime() - start) / 1_000_000;

        monitor.shutdownNow();
        sample();

        PoolStats after = pool.getStats();

        System.out.println("\n-------------------------");
        System.out.println("Elapsed            : " + elapsedMillis + " ms");
        System.out.println("Successful inserts : " + inserts.get());
        System.out.println("Successful selects : " + selects.get());
        System.out.println("Pool timeouts      : " + poolTimeouts.get());
        System.out.println("Other DB errors    : " + otherErrors.get());
        System.out.println("Max active seen    : " + maxActive.get()
                + " (pool size " + after.getMaximumPoolSize() + ")");
        System.out.println("Max waiting seen   : " + maxWaiting.get() + " thread(s)");
        System.out.println("Pool after         : " + after);
        System.out.println("-------------------------");

        System.out.println(maxActive.get() <= after.getMaximumPoolSize()
                ? "[OK] Never more than " + after.getMaximumPoolSize() + " connections in use."
                : "[PROBLEM] More connections in use than the pool size!");

        System.out.println(after.getActive() == 0
                ? "[OK] All borrowed connections were returned to the pool (active = 0)."
                : "[PROBLEM] " + after.getActive() + " connection(s) not returned!");
    }

    /*
    One worker: insert an event, then read it back, N times.
    Errors are counted and logged; the worker keeps going.
    */
    private void work(int workerNumber, int operations) {

        String workerName = Thread.currentThread().getName();

        for (int op = 1; op <= operations; op++) {

            try {

                long eventId = auditService.logEvent(
                        "WORKER_TASK",
                        "worker " + workerNumber + " (" + workerName + ") operation " + op);

                inserts.incrementAndGet();

                AuditEvent event = auditService.getEvent(eventId);

                if (event != null) {
                    selects.incrementAndGet();
                }

            } catch (PoolExhaustedException e) {

                // Already logged by the repository
                poolTimeouts.incrementAndGet();

            } catch (Exception e) {

                // Already logged by the repository (DB errors)
                otherErrors.incrementAndGet();
            }
        }
    }

    private void sample() {

        PoolStats stats = pool.getStats();

        maxActive.accumulateAndGet(stats.getActive(), Math::max);
        maxWaiting.accumulateAndGet(stats.getWaiting(), Math::max);
    }

    private void awaitQuietly(CountDownLatch latch) {

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
