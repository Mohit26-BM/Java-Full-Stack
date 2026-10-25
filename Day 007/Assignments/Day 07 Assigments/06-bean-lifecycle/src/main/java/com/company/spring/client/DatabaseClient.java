package com.company.spring.client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.company.spring.lifecycle.LifecycleLog;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

/*
A client that holds ONE open database connection for its whole life:

  constructor     -> object exists, but has NO dependencies yet
  setDataSource   -> Spring injects the DataSource
  @PostConstruct  -> dependencies are ready: OPEN the connection
  business method -> USE the connection
  @PreDestroy     -> container is closing: CLOSE the connection

Why setter injection here? Only to make the "dependencies injected"
phase visible as its own step between the constructor and
@PostConstruct. (With constructor injection the dependency arrives
inside the constructor itself - still before @PostConstruct.)

Each phase that has a connection also writes a row to
a06_lifecycle_events, so the order is recorded in the database too.
*/
@Component
public class DatabaseClient {

    private static final String NAME = "DatabaseClient";

    private final String runId = UUID.randomUUID().toString().substring(0, 8);

    private DataSource dataSource;
    private Connection connection;

    public DatabaseClient() {

        LifecycleLog.step(NAME, "constructor - dataSource is "
                + (dataSource == null ? "null (not injected yet)" : "set"));
    }

    @Autowired
    public void setDataSource(DataSource dataSource) {

        this.dataSource = dataSource;

        LifecycleLog.step(NAME, "dependency injected - DataSource set");
    }

    /*
    Called ONCE, after construction AND injection, before anyone can use
    the bean. The right place to open resources or validate settings.
    If it throws, the bean - and the whole container - fails to start.
    */
    @PostConstruct
    public void init() throws SQLException {

        connection = dataSource.getConnection();

        LifecycleLog.step(NAME, "@PostConstruct - connection OPENED to "
                + connection.getCatalog());

        recordEvent("POST_CONSTRUCT", "connection opened");
    }

    /*
    Business method: only works because @PostConstruct opened the connection.
    */
    public int recordBusinessEvent(String detail) throws SQLException {

        recordEvent("BUSINESS", detail);

        int count = countEventsOfThisRun();

        LifecycleLog.step(NAME, "business method - '" + detail
                + "' saved; rows for this run = " + count);

        return count;
    }

    /*
    Called ONCE when the container closes (singletons only).
    The right place to release resources.
    */
    @PreDestroy
    public void shutdown() throws SQLException {

        recordEvent("PRE_DESTROY", "closing connection");

        connection.close();

        LifecycleLog.step(NAME, "@PreDestroy - connection CLOSED (isClosed="
                + connection.isClosed() + ")");
    }

    public String getRunId() {
        return runId;
    }

    // =========================================================

    private void recordEvent(String phase, String detail) throws SQLException {

        String sql = "INSERT INTO a06_lifecycle_events (run_id, bean_name, phase, detail) "
                   + "VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, runId);
            statement.setString(2, NAME);
            statement.setString(3, phase);
            statement.setString(4, detail);

            statement.executeUpdate();
        }
    }

    private int countEventsOfThisRun() throws SQLException {

        String sql = "SELECT COUNT(*) FROM a06_lifecycle_events WHERE run_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, runId);

            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1);
            }
        }
    }
}
