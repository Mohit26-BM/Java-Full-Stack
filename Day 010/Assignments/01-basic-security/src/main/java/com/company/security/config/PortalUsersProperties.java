package com.company.security.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

/**
 * The demo users, read from application.properties (portal.users[0].username=...).
 *
 * Only BCrypt HASHES are stored there, never the plain passwords. @Validated
 * makes the application refuse to start if a password is not a BCrypt hash
 * ($2a$, $2b$ or $2y$ followed by the cost), so a plain-text password can never
 * slip into the configuration by accident.
 */
@Validated
@ConfigurationProperties(prefix = "portal")
public record PortalUsersProperties(@NotEmpty List<@Valid UserEntry> users) {

    public record UserEntry(
            @NotBlank String username,
            @Pattern(regexp = "^\\$2[aby]\\$\\d{2}\\$.{53}$", message = "must be a BCrypt hash, not a plain password")
            String passwordHash,
            @NotEmpty List<String> roles) {
    }
}
