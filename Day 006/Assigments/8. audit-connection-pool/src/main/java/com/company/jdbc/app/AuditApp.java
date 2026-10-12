package com.company.jdbc.app;

import java.sql.SQLException;
import java.util.List;

import com.company.jdbc.config.ConnectionPool;
import com.company.jdbc.exception.DataAccessException;
import com.company.jdbc.exception.InvalidInputException;
import com.company.jdbc.model.AuditEvent;
import com.company.jdbc.repository.AuditEventRepository;
import com.company.jdbc.repository.JdbcAuditEventRepository;
import com.company.jdbc.service.AuditService;
import com.company.jdbc.service.AuditServiceImpl;

public class AuditApp {

    private static final String LINE =
            "--------------------------------------------------------------------------";

    private static ConnectionPool pool;
    private static AuditEventRepository repository;
    private static AuditService auditService;

    public static void main(String[] args) {

        /*
        Configuration (pool) is created here, once, and handed to the
        repository as a plain DataSource. try-with-resources closes
        the pool - and all its physical connections - on exit.
        */
        try (ConnectionPool connectionPool = createPool()) {

            if (connectionPool == null) {
                return;
            }

            pool = connectionPool;
            repository = new JdbcAuditEventRepository(pool.getDataSource());
            auditService = new AuditServiceImpl(repository);

            runMenu();
        }

        System.out.println("Connection pool closed. Goodbye.");
    }

    private static ConnectionPool createPool() {

        try {

            ConnectionPool connectionPool = ConnectionPool.create();

            System.out.println("Connection pool started: " + connectionPool.getStats());

            return connectionPool;

        } catch (RuntimeException e) {

            System.out.println("Could not start the connection pool: " + e.getMessage());
            System.out.println("Check that MySQL is running, schema.sql was run, "
                    + "and db.properties is correct.");

            return null;
        }
    }

    private static void runMenu() {

        while (true) {

            showMenu();

            int choice = ConsoleInput.readPositiveInt("Enter your choice: ");

            if (choice == 8) {
                return;
            }

            try {

                handleChoice(choice);

            } catch (DataAccessException e) {

                // The repository has already logged the details.
                System.out.println("\nDatabase Error: " + e.getMessage());

            } catch (SQLException e) {

                System.out.println("\nDatabase Error: " + e.getMessage());

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
                System.out.println("\nInterrupted.");
            }
        }
    }

    private static void showMenu() {

        System.out.println("\n========================================");
        System.out.println("   AUDIT EVENTS - DATASOURCE & POOLING");
        System.out.println("========================================");
        System.out.println("1. Log an Audit Event");
        System.out.println("2. View Recent Events");
        System.out.println("3. Pool Status");
        System.out.println("4. Multi-threaded Load Test");
        System.out.println("5. Pool Exhaustion Demo");
        System.out.println("6. Failure Test (connection returned after error)");
        System.out.println("7. Benchmark: DriverManager vs Pool");
        System.out.println("8. Exit");
        System.out.println("========================================");
    }

    private static void handleChoice(int choice)
            throws SQLException, InterruptedException {

        switch (choice) {
            case 1: logEvent();       break;
            case 2: viewRecent();     break;
            case 3: poolStatus();     break;
            case 4: loadTest();       break;
            case 5: new PoolExhaustionDemo(auditService, pool).run(); break;
            case 6: failureTest();    break;
            case 7: benchmark();      break;
            default:
                System.out.println("\nInvalid choice. Please select 1 to 8.");
        }
    }

    // =========================================================
    // MENU ACTIONS
    // =========================================================

    private static void logEvent() {

        System.out.println("\n========== Log Audit Event ==========");

        String type = ConsoleInput.readLine("Event type (e.g. USER_LOGIN): ");
        String message = ConsoleInput.readLine("Message (optional): ");

        try {

            long eventId = auditService.logEvent(type, message);

            System.out.println("\nEvent saved with ID " + eventId + ".");
            System.out.println("Pool: " + pool.getStats());

        } catch (InvalidInputException e) {

            System.out.println("\nValidation Error: " + e.getMessage());
        }
    }

    private static void viewRecent() {

        System.out.println("\n========== Recent Audit Events ==========");

        int limit = ConsoleInput.readPositiveInt("How many (Enter for 15): ", 15);

        List<AuditEvent> events = auditService.getRecentEvents(limit);

        System.out.println("\nTotal events in table: " + auditService.countEvents());
        System.out.println(LINE);
        System.out.println(String.format("%-6s %-19s %-18s %s", "ID", "Created", "Type", "Message"));
        System.out.println(LINE);

        for (AuditEvent event : events) {
            System.out.println(event);
        }

        System.out.println(LINE);
    }

    private static void poolStatus() {

        System.out.println("\n========== Pool Status ==========");
        System.out.println(pool.getStats());
        System.out.println("Connection timeout: " + pool.getConnectionTimeoutMillis() + " ms");
    }

    private static void loadTest() throws InterruptedException {

        System.out.println("\n========== Multi-threaded Load Test ==========");
        System.out.println("Use more threads than the pool size (5) to see threads wait.");

        int threads = ConsoleInput.readPositiveInt("Worker threads (Enter for 10): ", 10);
        int operations = ConsoleInput.readPositiveInt("Operations per thread (Enter for 5): ", 5);

        new WorkerLoadTest(auditService, pool).run(threads, operations);
    }

    /*
    Acceptance test: resources are closed on failure too.

    Saves an event with a NULL event_type. MySQL rejects it
    (event_type is NOT NULL), the repository logs the error,
    and try-with-resources still returns the connection.
    The service is bypassed on purpose, because it would
    catch the problem before the database.
    */
    private static void failureTest() {

        System.out.println("\n========== Failure Test ==========");
        System.out.println("Pool before : " + pool.getStats());

        try {

            repository.save(new AuditEvent(null, "This insert must fail"));

            System.out.println("Unexpected: the invalid insert succeeded.");

        } catch (DataAccessException e) {

            System.out.println("Expected failure: " + e.getMessage());
        }

        int active = pool.getStats().getActive();

        System.out.println("Pool after  : " + pool.getStats());
        System.out.println(active == 0
                ? "[OK] The connection was returned to the pool even though the SQL failed."
                : "[PROBLEM] A connection was not returned!");
    }

    private static void benchmark() throws SQLException {

        System.out.println("\n========== DriverManager vs Pool ==========");

        int iterations = ConsoleInput.readPositiveInt("Iterations (Enter for 200): ", 200);

        new ConnectionBenchmark(pool).run(iterations);
    }
}
