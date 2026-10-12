package com.company.jdbc.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.company.jdbc.exception.DataAccessException;
import com.company.jdbc.exception.PoolExhaustedException;
import com.company.jdbc.model.AuditEvent;

/*
Repository that gets every connection from a DataSource.

It does NOT know which pool is used, nor its size or timeouts:
that is configuration, kept in ConnectionPool / pool.properties.

THE KEY RULE OF POOLING
-----------------------
dataSource.getConnection() BORROWS a connection from the pool.
connection.close() does NOT close the physical connection;
it RETURNS it to the pool for the next caller.

Every method therefore opens the connection in try-with-resources,
so close() - i.e. "return to pool" - runs on success AND on failure.
A connection that is never closed is a LEAK: the pool loses one
connection for good, and after 5 leaks every caller times out.
*/
public class JdbcAuditEventRepository implements AuditEventRepository {

    private static final Logger log =
            LoggerFactory.getLogger(JdbcAuditEventRepository.class);

    private final DataSource dataSource;

    /*
    The DataSource is passed in (constructor injection),
    the repository never creates it.
    */
    public JdbcAuditEventRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public long save(AuditEvent event) {

        String sql = "INSERT INTO audit_events (event_type, event_message) "
                   + "VALUES (?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, event.getEventType());

            if (event.getEventMessage() == null) {
                statement.setNull(2, Types.VARCHAR);
            } else {
                statement.setString(2, event.getEventMessage());
            }

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {

                keys.next();
                long eventId = keys.getLong(1);

                log.info("Saved audit event {} (type={})", eventId, event.getEventType());

                return eventId;
            }

        } catch (SQLException e) {

            throw translate("save audit event (type=" + event.getEventType() + ")", e);
        }
    }

    @Override
    public AuditEvent findById(long eventId) {

        String sql = "SELECT event_id, event_type, event_message, created_at "
                   + "FROM audit_events "
                   + "WHERE event_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, eventId);

            try (ResultSet resultSet = statement.executeQuery()) {

                AuditEvent event = resultSet.next() ? mapRow(resultSet) : null;

                log.info("Read audit event {} ({})", eventId,
                        event == null ? "not found" : "found");

                return event;
            }

        } catch (SQLException e) {

            throw translate("read audit event " + eventId, e);
        }
    }

    @Override
    public List<AuditEvent> findRecent(int limit) {

        String sql = "SELECT event_id, event_type, event_message, created_at "
                   + "FROM audit_events "
                   + "ORDER BY event_id DESC "
                   + "LIMIT ?";

        List<AuditEvent> events = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, limit);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    events.add(mapRow(resultSet));
                }
            }

            log.info("Read {} recent audit event(s)", events.size());

        } catch (SQLException e) {

            throw translate("read recent audit events", e);
        }

        return events;
    }

    @Override
    public long countAll() {

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement("SELECT COUNT(*) FROM audit_events");
             ResultSet resultSet = statement.executeQuery()) {

            resultSet.next();

            return resultSet.getLong(1);

        } catch (SQLException e) {

            throw translate("count audit events", e);
        }
    }

    // =========================================================
    // HELPERS
    // =========================================================

    /*
    Logs the database error and converts it.

    HikariCP throws SQLTransientConnectionException when no connection
    became free within connectionTimeout. That case gets its own
    exception type so callers can handle pool exhaustion predictably.
    */
    private DataAccessException translate(String operation, SQLException e) {

        if (e instanceof SQLTransientConnectionException) {

            log.error("POOL EXHAUSTED while trying to {}: {}", operation, e.getMessage());

            return new PoolExhaustedException(
                    "No database connection available (pool exhausted) - "
                    + "could not " + operation, e);
        }

        log.error("Database error while trying to {}: [{}] {}",
                operation, e.getErrorCode(), e.getMessage());

        return new DataAccessException("Could not " + operation + ": " + e.getMessage(), e);
    }

    private AuditEvent mapRow(ResultSet resultSet) throws SQLException {

        AuditEvent event = new AuditEvent(
                resultSet.getString("event_type"),
                resultSet.getString("event_message")
        );

        event.setEventId(resultSet.getLong("event_id"));

        Timestamp createdAt = resultSet.getTimestamp("created_at");
        event.setCreatedAt(createdAt == null ? null : createdAt.toLocalDateTime());

        return event;
    }
}
