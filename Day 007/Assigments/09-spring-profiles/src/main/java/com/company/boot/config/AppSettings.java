package com.company.boot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Binds the "app.*" properties.
 * Values come from application.properties, overridden by
 * application-{profile}.properties for the active profile(s).
 */
@ConfigurationProperties(prefix = "app")
public class AppSettings {

    private String message;
    private String environment;
    private boolean showSqlLogs;
    private int cacheTtlSeconds;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public boolean isShowSqlLogs() {
        return showSqlLogs;
    }

    public void setShowSqlLogs(boolean showSqlLogs) {
        this.showSqlLogs = showSqlLogs;
    }

    public int getCacheTtlSeconds() {
        return cacheTtlSeconds;
    }

    public void setCacheTtlSeconds(int cacheTtlSeconds) {
        this.cacheTtlSeconds = cacheTtlSeconds;
    }
}
