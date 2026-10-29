package com.example.bank.auth;

/** What the admin endpoints return about a user. Never includes the password hash. */
public record UserSummary(String username, String role, Long customerId) {

    static UserSummary from(UserCredential u) {
        return new UserSummary(u.getUsername(), u.getRole().name(), u.getCustomerId());
    }
}
