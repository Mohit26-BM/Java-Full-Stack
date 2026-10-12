package com.company.jdbc.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/*
Connection settings come from db.properties (src/main/resources),
so the password is not hard-coded in Java code.

Every call returns a NEW connection; the caller closes it
(always with try-with-resources).
*/
public final class DBConnection {

    private static final Properties PROPERTIES = loadProperties();

    private DBConnection() {
    }

    private static Properties loadProperties() {

        Properties properties = new Properties();

        try (InputStream input =
                     DBConnection.class
                             .getClassLoader()
                             .getResourceAsStream("db.properties")) {

            if (input == null) {
                throw new IllegalStateException(
                        "db.properties was not found on the classpath."
                );
            }

            properties.load(input);

        } catch (IOException e) {

            throw new IllegalStateException("Unable to read db.properties", e);
        }

        return properties;
    }

    public static Connection getConnection() throws SQLException {
        return getConnection(false);
    }

    /*
    rewriteBatchedStatements=true lets the MySQL driver turn a JDBC
    batch of INSERTs into ONE multi-row INSERT statement.
    Used only by the benchmark, to show its effect.
    */
    public static Connection getConnection(boolean rewriteBatchedStatements)
            throws SQLException {

        String url = PROPERTIES.getProperty("db.url");

        if (rewriteBatchedStatements) {
            url += (url.contains("?") ? "&" : "?") + "rewriteBatchedStatements=true";
        }

        return DriverManager.getConnection(
                url,
                PROPERTIES.getProperty("db.user"),
                PROPERTIES.getProperty("db.password")
        );
    }
}
