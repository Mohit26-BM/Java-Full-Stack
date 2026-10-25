package com.company.boot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Approach 1: @ConfigurationProperties.
 *
 * Binds the whole "app" group in one go:
 *
 *   app.name       -> name
 *   app.version    -> version
 *   app.owner      -> owner
 *   app.max-users  -> maxUsers     (relaxed binding: kebab-case -> camelCase)
 *   app.environment-> environment
 *
 * @Validated makes Spring check the constraints at startup, so a bad value
 * stops the application instead of reaching a REST response.
 */
@ConfigurationProperties(prefix = "app")
@Validated
public class AppProperties {

    @NotBlank
    private String name;

    @NotBlank
    private String version;

    @NotBlank
    private String owner;

    @Min(1)
    private int maxUsers = 10;          // default when app.max-users is absent

    private String environment = "local";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getMaxUsers() {
        return maxUsers;
    }

    public void setMaxUsers(int maxUsers) {
        this.maxUsers = maxUsers;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }
}
