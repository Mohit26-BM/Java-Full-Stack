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

Every call returns a NEW connection. The caller must close it
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

        return DriverManager.getConnection(
                PROPERTIES.getProperty("db.url"),
                PROPERTIES.getProperty("db.user"),
                PROPERTIES.getProperty("db.password")
        );
    }
}
