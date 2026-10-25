package com.company.rbac.security;

/**
 * The job roles. A role is stored as the authority "ROLE_" + name, e.g. ROLE_ADMIN,
 * and checked with hasRole("ADMIN"), which adds the prefix itself.
 *
 * Because the values are an enum, application.properties cannot contain
 * "ROLE_ADMIN" (or a typo): the application would refuse to start.
 */
public enum Role {
    ADMIN,
    HR,
    MANAGER,
    USER;

    public String authority() {
        return "ROLE_" + name();
    }
}
