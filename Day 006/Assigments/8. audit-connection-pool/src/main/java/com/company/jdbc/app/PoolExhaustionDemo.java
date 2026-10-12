package com.company.jdbc.app;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.company.jdbc.config.ConnectionPool;
import com.company.jdbc.exception.PoolExhaustedException;
import com.company.jdbc.service.AuditService;

/*
Shows what happens when every pooled connection is in use.

1. Borrow ALL connections and hold them (like 5 slow requests).
2. Ask the repository to do one more query.
   -> it waits connectionTimeout (3 s), then fails with
      PoolExhaustedException. Predictable: no hang, clear error.
3. Return the held connections (finally block).
4. The same query now succeeds immediately.
*/
public class PoolExhaustionDemo {

    private final AuditService auditService;
    private final ConnectionPool pool;

    public PoolExhaustionDemo(AuditService auditService, ConnectionPool pool) {
        this.auditService = auditService;
        this.pool = pool;
    }

    public void run() throws SQLException {

        int poolSize = pool.getStats().getMaximumPoolSize();

        System.out.println("\nPool before            : " + pool.getStats());

        List<Connection> held = new ArrayList<>();

        try {

            // ---------- 1. borrow every connection ----------

            for (int i = 0; i < poolSize; i++) {
                held.add(pool.getDataSource().getConnection());
            }

            System.out.println("Holding " + held.size() + " connections : " + pool.getStats());

            // ---------- 2. one more request ----------

            System.out.println("\nRequesting one more connection (timeout "
                    + pool.getConnectionTimeoutMillis() + " ms)...");

            long start = System.currentTimeMillis();

            try {

                auditService.countEvents();

                System.out.println("Unexpected: a connection was available.");

            } catch (PoolExhaustedException e) {

                long waited = System.currentTimeMillis() - start;

                System.out.println("Caught PoolExhaustedException after " + waited + " ms:");
                System.out.println("  " + e.getMessage());
                System.out.println("[OK] Exhaustion handled predictably: bounded wait, clear error.");
            }

        } finally {

            // ---------- 3. ALWAYS return what was borrowed ----------

            for (Connection connection : held) {
                connection.close();     // returns it to the pool
            }
        }

        System.out.println("\nReturned " + held.size() + " connections : " + pool.getStats());

        // ---------- 4. works again ----------

        long count = auditService.countEvents();

        System.out.println("Same query now succeeds: " + count + " audit events in the table.");
        System.out.println("Pool after             : " + pool.getStats());
    }
}
