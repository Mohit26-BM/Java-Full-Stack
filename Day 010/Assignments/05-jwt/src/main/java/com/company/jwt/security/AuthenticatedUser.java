package com.company.jwt.security;

import java.time.Instant;

public record AuthenticatedUser(String username, String tokenId, Instant tokenExpiresAt) {
    @Override
    public String toString() {
        return username;
    }
}
