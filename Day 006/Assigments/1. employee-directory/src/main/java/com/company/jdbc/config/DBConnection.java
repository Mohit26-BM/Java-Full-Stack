package com.company.jdbc.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/*
Loads the connection settings from db.properties (src/main/resources)
so the password is not hard-coded inside Java code.
*/
public class DBConnection {

    private static final String PROPERTIES_FILE = "db.properties";

    private static final Properties PROPERTIES = loadProperties();

    private DBConnection() {
    }

    private static Properties loadProperties() {

        Properties properties = new Properties();

        try (InputStream input =
                     DBConnection.class
                             .getClassLoader()
                             .getResourceAsStream(PROPERTIES_FILE)) {

            if (input == null) {
                throw new IllegalStateException(
                        PROPERTIES_FILE + " was not found on the classpath."
                );
            }

            properties.load(input);

        } catch (IOException e) {

            throw new IllegalStateException(
                    "Unable to read " + PROPERTIES_FILE, e
            );
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
