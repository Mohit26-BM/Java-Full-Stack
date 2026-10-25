package com.company.rbac.security;

public enum Role {
    ADMIN,
    HR,
    MANAGER,
    USER;

    public String authority() {
        return "ROLE_" + name();
    }
}
