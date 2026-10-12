package com.company.jdbc.config;

import java.util.Properties;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;

/*
CONFIGURATION ONLY - no business or SQL code here.

Builds the HikariCP connection pool from db.properties (credentials)
and pool.properties (pool tuning), and exposes it as a standard
javax.sql.DataSource.

The repository only ever sees the DataSource interface, so the pool
library could be swapped (e.g. for Apache DBCP) without changing it.
*/
public final class ConnectionPool implements AutoCloseable {

    private final HikariDataSource dataSource;

    private ConnectionPool(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /*
    Creates the pool. HikariCP opens the minimumIdle connections
    immediately, so a wrong password or a stopped MySQL fails here,
    at startup, instead of on the first request.
    */
    public static ConnectionPool create() {

        DbSettings db = DbSettings.load();
        Properties pool = PropertiesLoader.load("pool.properties");

        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(db.getUrl());
        config.setUsername(db.getUser());
        config.setPassword(db.getPassword());

        config.setPoolName(pool.getProperty("pool.name", "AuditPool"));

        config.setMaximumPoolSize(
                (int) PropertiesLoader.requireLong(pool, "pool.maximumPoolSize"));

        config.setMinimumIdle(
                (int) PropertiesLoader.requireLong(pool, "pool.minimumIdle"));

        config.setConnectionTimeout(
                PropertiesLoader.requireLong(pool, "pool.connectionTimeoutMs"));

        config.setIdleTimeout(
                PropertiesLoader.requireLong(pool, "pool.idleTimeoutMs"));

        config.setMaxLifetime(
                PropertiesLoader.requireLong(pool, "pool.maxLifetimeMs"));

        config.setLeakDetectionThreshold(
                PropertiesLoader.requireLong(pool, "pool.leakDetectionThresholdMs"));

        return new ConnectionPool(new HikariDataSource(config));
    }

    /*
    What the rest of the application uses.
    */
    public DataSource getDataSource() {
        return dataSource;
    }

    public PoolStats getStats() {

        HikariPoolMXBean pool = dataSource.getHikariPoolMXBean();

        return new PoolStats(
                pool.getActiveConnections(),
                pool.getIdleConnections(),
                pool.getTotalConnections(),
                pool.getThreadsAwaitingConnection(),
                dataSource.getMaximumPoolSize()
        );
    }

    public long getConnectionTimeoutMillis() {
        return dataSource.getConnectionTimeout();
    }

    /*
    Closes all physical connections. Call once when the application ends.
    */
    @Override
    public void close() {
        dataSource.close();
    }
}
