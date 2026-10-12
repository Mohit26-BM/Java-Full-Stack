package com.company.jdbc.config;

import java.util.Properties;

/*
Database URL and credentials from db.properties.
Used by the pool, and by the DriverManager side of the benchmark.
*/
public final class DbSettings {

    private final String url;
    private final String user;
    private final String password;

    private DbSettings(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public static DbSettings load() {

        Properties properties = PropertiesLoader.load("db.properties");

        return new DbSettings(
                properties.getProperty("db.url"),
                properties.getProperty("db.user"),
                properties.getProperty("db.password")
        );
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}
