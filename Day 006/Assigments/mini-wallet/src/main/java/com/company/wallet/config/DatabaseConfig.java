package com.company.wallet.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/*
CONFIGURATION ONLY.

The ONLY class that reads db.properties / pool.properties and the
only class that knows HikariCP is used. Everything else receives a
plain javax.sql.DataSource, so no business class ever sees the
database URL or password.
*/
public final class DatabaseConfig implements AutoCloseable {

    private final HikariDataSource dataSource;

    private DatabaseConfig(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /*
    Builds the pool. HikariCP opens connections immediately, so a
    stopped MySQL or a wrong password fails here, at startup.
    */
    public static DatabaseConfig create() {

        Properties db = load("db.properties");
        Properties pool = load("pool.properties");

        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(require(db, "db.url"));
        config.setUsername(require(db, "db.user"));
        config.setPassword(require(db, "db.password"));

        config.setPoolName(pool.getProperty("pool.name", "WalletPool"));
        config.setMaximumPoolSize(Integer.parseInt(require(pool, "pool.maximumPoolSize")));
        config.setMinimumIdle(Integer.parseInt(require(pool, "pool.minimumIdle")));
        config.setConnectionTimeout(Long.parseLong(require(pool, "pool.connectionTimeoutMs")));
        config.setIdleTimeout(Long.parseLong(require(pool, "pool.idleTimeoutMs")));
        config.setMaxLifetime(Long.parseLong(require(pool, "pool.maxLifetimeMs")));
        config.setLeakDetectionThreshold(
                Long.parseLong(require(pool, "pool.leakDetectionThresholdMs")));

        return new DatabaseConfig(new HikariDataSource(config));
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    /*
    For display / demos only.
    */
    public int getActiveConnections() {
        return dataSource.getHikariPoolMXBean().getActiveConnections();
    }

    @Override
    public void close() {
        dataSource.close();
    }

    private static Properties load(String fileName) {

        Properties properties = new Properties();

        try (InputStream input =
                     DatabaseConfig.class.getClassLoader().getResourceAsStream(fileName)) {

            if (input == null) {
                throw new IllegalStateException(fileName + " was not found on the classpath.");
            }

            properties.load(input);

        } catch (IOException e) {

            throw new IllegalStateException("Unable to read " + fileName, e);
        }

        return properties;
    }

    private static String require(Properties properties, String key) {

        String value = properties.getProperty(key);

        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException("Missing setting: " + key);
        }

        return value.trim();
    }
}
