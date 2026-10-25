package com.company.jwt.security;

import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class RevokedAccessTokens {
    private final Map<String, Instant> revoked = new ConcurrentHashMap<>();
    private final Clock clock;

    public RevokedAccessTokens(Clock clock) {
        this.clock = clock;
    }

    public void revoke(String tokenId, Instant tokenExpiresAt) {
        revoked.put(tokenId, tokenExpiresAt);
        Instant now = clock.instant();
        revoked.values().removeIf(expiry -> expiry.isBefore(now));
    }

    public boolean isRevoked(String tokenId) {
        return revoked.containsKey(tokenId);
    }
}
