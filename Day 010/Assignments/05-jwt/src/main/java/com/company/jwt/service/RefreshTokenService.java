package com.company.jwt.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.jwt.config.JwtProperties;
import com.company.jwt.entity.AppUser;
import com.company.jwt.entity.RefreshToken;
import com.company.jwt.entity.RefreshToken.RevokedReason;
import com.company.jwt.exception.ApiException;
import com.company.jwt.repository.RefreshTokenRepository;

@Service
public class RefreshTokenService {
    private static final Logger log = LoggerFactory.getLogger(RefreshTokenService.class);

    private final SecureRandom random = new SecureRandom();
    private final RefreshTokenRepository tokens;
    private final JwtProperties properties;
    private final Clock clock;

    public RefreshTokenService(RefreshTokenRepository tokens, JwtProperties properties, Clock clock) {
        this.tokens = tokens;
        this.properties = properties;
        this.clock = clock;
    }

    @Transactional
    public String issue(AppUser user) {
        return issue(user, UUID.randomUUID().toString());
    }

    @Transactional(noRollbackFor = ApiException.class)
    public Rotation rotate(String rawToken) {
        LocalDateTime now = LocalDateTime.now(clock);
        RefreshToken current = tokens.findByTokenHash(sha256(rawToken))
                .orElseThrow(() -> ApiException.unauthorized("Refresh token is invalid"));

        if (current.isRevoked()) {
            if (current.getRevokedReason() == RevokedReason.ROTATED) {
                revokeFamily(current.getFamilyId(), RevokedReason.REUSE_DETECTED, now);
                log.warn("Refresh token reuse detected for {}: session {} revoked",
                        current.getUser().getUsername(), current.getFamilyId());
                throw ApiException.unauthorized("Refresh token reuse detected; please log in again");
            }
            throw ApiException.unauthorized("Refresh token has been revoked");
        }
        if (current.isExpiredAt(now)) {
            throw ApiException.unauthorized("Refresh token has expired");
        }

        current.revoke(RevokedReason.ROTATED, now);
        String next = issue(current.getUser(), current.getFamilyId());
        return new Rotation(current.getUser(), next);
    }

    public record Rotation(AppUser user, String newRawToken) {
    }

    @Transactional
    public void revokeSession(String rawToken, String username) {
        tokens.findByTokenHash(sha256(rawToken))
                .filter(token -> token.getUser().getUsername().equals(username))
                .ifPresent(token -> revokeFamily(token.getFamilyId(), RevokedReason.LOGOUT, LocalDateTime.now(clock)));
    }

    @Transactional
    public int revokeAll(Long userId) {
        LocalDateTime now = LocalDateTime.now(clock);
        var active = tokens.findByUserIdAndRevokedAtIsNull(userId);
        active.forEach(token -> token.revoke(RevokedReason.ADMIN, now));
        return active.size();
    }

    private String issue(AppUser user, String familyId) {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String raw = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        LocalDateTime now = LocalDateTime.now(clock);
        tokens.save(new RefreshToken(user, sha256(raw), familyId, now.plus(properties.refreshTokenTtl()), now));
        return raw;
    }

    private void revokeFamily(String familyId, RevokedReason reason, LocalDateTime now) {
        tokens.findByFamilyIdAndRevokedAtIsNull(familyId).forEach(token -> token.revoke(reason, now));
    }

    static String sha256(String value) {
        try {
            return HexFormat.of().formatHex(
                    MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is always available in Java", e);
        }
    }
}
