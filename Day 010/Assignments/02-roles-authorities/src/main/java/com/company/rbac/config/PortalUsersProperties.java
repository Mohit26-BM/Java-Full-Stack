package com.company.rbac.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import com.company.rbac.security.Permission;
import com.company.rbac.security.Role;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * The demo users, read from application.properties (portal.users[0].username=...).
 *
 * Each user has ROLES and PERMISSIONS, listed separately. Only BCrypt hashes are
 * stored, and the application refuses to start if a password is not a hash.
 * employeeId links the login to an employee record (used for "my team").
 */
@Validated
@ConfigurationProperties(prefix = "portal")
public record PortalUsersProperties(@NotEmpty List<@Valid UserEntry> users) {

    public record UserEntry(
            @NotBlank String username,
            @Pattern(regexp = "^\\$2[aby]\\$\\d{2}\\$.{53}$", message = "must be a BCrypt hash, not a plain password")
            String passwordHash,
            @NotNull List<Role> roles,              // may be empty: an integration account has no job role
            @NotNull List<Permission> permissions,
            Long employeeId) {                      // null for accounts that are not a person
    }
}
