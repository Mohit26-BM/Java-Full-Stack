package com.company.jdbc.app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.company.jdbc.config.ConnectionPool;
import com.company.jdbc.config.DbSettings;

/*
OPTIONAL CHALLENGE: DriverManager vs connection pool.

Each iteration = get a connection, run "SELECT 1", close it.

- DriverManager: every getConnection() opens a NEW physical
  connection (TCP handshake, MySQL authentication, session setup)
  and close() tears it down again.

- Pool: getConnection() hands out an already-open connection and
  close() just puts it back, so almost no time is spent connecting.
*/
public class ConnectionBenchmark {

    private static final String QUERY = "SELECT 1";

    private final ConnectionPool pool;
    private final DbSettings db;

    public ConnectionBenchmark(ConnectionPool pool) {
        this.pool = pool;
        this.db = DbSettings.load();
    }

    public void run(int iterations) throws SQLException {

        // Warm-up so class loading is not counted
        runDriverManager(3);
        runPool(3);

        System.out.println("\nRunning " + iterations + " iterations each...");

        long driverManagerMillis = runDriverManager(iterations);
        long poolMillis = runPool(iterations);

        System.out.println("\n-------------------------");
        System.out.println(String.format("%-14s %10s %14s", "Approach", "Total ms", "Avg ms / op"));
        System.out.println(String.format("%-14s %10d %14.3f", "DriverManager",
                driverManagerMillis, (double) driverManagerMillis / iterations));
        System.out.println(String.format("%-14s %10d %14.3f", "Pool",
                poolMillis, (double) poolMillis / iterations));
        System.out.println("-------------------------");

        if (poolMillis > 0) {
            System.out.println(String.format("The pool was about %.1fx faster.",
                    (double) driverManagerMillis / poolMillis));
        } else {
            System.out.println("The pool took under 1 ms in total.");
        }

        System.out.println("Pool after: " + pool.getStats());
    }

    private long runDriverManager(int iterations) throws SQLException {

        long start = System.nanoTime();

        for (int i = 0; i < iterations; i++) {

            try (Connection connection =
                         DriverManager.getConnection(db.getUrl(), db.getUser(), db.getPassword())) {

                runQuery(connection);
            }
        }

        return (System.nanoTime() - start) / 1_000_000;
    }

    private long runPool(int iterations) throws SQLException {

        long start = System.nanoTime();

        for (int i = 0; i < iterations; i++) {

            try (Connection connection = pool.getDataSource().getConnection()) {

                runQuery(connection);
            }
        }

        return (System.nanoTime() - start) / 1_000_000;
    }

    private void runQuery(Connection connection) throws SQLException {

        try (PreparedStatement statement = connection.prepareStatement(QUERY);
             ResultSet resultSet = statement.executeQuery()) {

            resultSet.next();
        }
    }
}
