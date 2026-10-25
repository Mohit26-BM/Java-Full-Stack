package com.company.spring.extension;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.company.spring.lifecycle.LifecycleLog;

/*
OPTIONAL EXTENSION: a class with NO Spring or jakarta annotations -
think of a class from a library jar that you cannot edit.

Its start/stop methods are plain methods (connect / disconnect).
Spring is told to call them through
    @Bean(initMethod = "connect", destroyMethod = "disconnect")
in ExtensionConfig.
*/
public class LegacyDatabaseClient {

    private static final String NAME = "LegacyDatabaseClient";

    private final DataSource dataSource;
    private Connection connection;

    public LegacyDatabaseClient(DataSource dataSource) {

        this.dataSource = dataSource;

        LifecycleLog.step(NAME, "constructor (DataSource via constructor)");
    }

    public void connect() throws SQLException {

        connection = dataSource.getConnection();

        LifecycleLog.step(NAME, "initMethod connect() - connection OPENED");
    }

    public boolean ping() throws SQLException {

        boolean valid = connection.isValid(2);

        LifecycleLog.step(NAME, "business method ping() - connection valid = " + valid);

        return valid;
    }

    public void disconnect() throws SQLException {

        connection.close();

        LifecycleLog.step(NAME, "destroyMethod disconnect() - connection CLOSED");
    }
}
