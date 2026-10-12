package com.company.jdbc.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/*
Connection settings come from db.properties,
so the password is not hard-coded in Java code.
*/
public final class DBConnection {

    private static final Properties PROPERTIES =
            PropertiesLoader.load("db.properties");

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {

        return DriverManager.getConnection(
                PROPERTIES.getProperty("db.url"),
                PROPERTIES.getProperty("db.user"),
                PROPERTIES.getProperty("db.password")
        );
    }
}
