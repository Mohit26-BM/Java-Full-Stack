package com.company.spring.app;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.company.spring.client.DatabaseClient;
import com.company.spring.config.AppConfig;

/*
Main program: start the context, call the business method, then
EXPLICITLY close the context - and finally read back the rows the
bean wrote to a06_lifecycle_events during this run.
*/
public class LifecycleApp {

    public static void main(String[] args) throws SQLException {

        System.out.println("main: creating the Spring context...");

        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);

        System.out.println("main: context started - DatabaseClient is ready to use\n");

        DatabaseClient client = context.getBean(DatabaseClient.class);

        System.out.println("main: calling the business method twice...");
        client.recordBusinessEvent("customer statement generated");
        client.recordBusinessEvent("monthly report exported");

        // Keep these for reading the table after the context is gone
        DataSource dataSource = context.getBean(DataSource.class);
        String runId = client.getRunId();

        System.out.println("\nmain: calling context.close() explicitly...");
        context.close();
        System.out.println("main: context closed\n");

        printRecordedEvents(dataSource, runId);
    }

    /*
    Evidence from the database: the order the phases were recorded in.
    */
    private static void printRecordedEvents(DataSource dataSource, String runId) throws SQLException {

        String sql = "SELECT event_id, phase, detail, logged_at FROM a06_lifecycle_events "
                   + "WHERE run_id = ? ORDER BY event_id";

        System.out.println("Rows written to a06_lifecycle_events in this run (run_id " + runId + "):");
        System.out.println("   ----------------------------------------------------------------------");

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, runId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    System.out.println(String.format("   %-4d %-15s %-30s %s",
                            resultSet.getInt("event_id"),
                            resultSet.getString("phase"),
                            resultSet.getString("detail"),
                            resultSet.getTimestamp("logged_at")));
                }
            }
        }

        System.out.println("   ----------------------------------------------------------------------");
    }
}
