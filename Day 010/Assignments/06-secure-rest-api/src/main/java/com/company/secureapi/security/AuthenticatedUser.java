package com.company.secureapi.security;

public record AuthenticatedUser(String username, Long employeeId) {
    @Override
    public String toString() {
        return username;
    }
}
