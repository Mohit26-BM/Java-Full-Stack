package com.company.dbauth.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.dbauth.config.PortalSecurityProperties;
import com.company.dbauth.entity.AppUser;
import com.company.dbauth.entity.PasswordResetToken;
import com.company.dbauth.exception.ApiException;
import com.company.dbauth.repository.AppUserRepository;
import com.company.dbauth.repository.PasswordResetTokenRepository;

/**
 * "Forgot my password" in two steps:
 *
 *   1. request(email)  -> a random one-time token, valid 15 minutes. Its SHA-256
 *                         hash is stored; the token itself would be e-mailed.
 *   2. confirm(token, newPassword) -> the password is replaced, the token is
 *                         marked used, and any login lock is cleared.
 *
 * Why SHA-256 for the token but BCrypt for passwords? The token is 256 random
 * bits, impossible to guess, so a fast hash is enough. Passwords are chosen by
 * people and guessable, so they need a deliberately slow hash.
 */
@Service
public class PasswordResetService {

    private static final Logger log = LoggerFactory.getLogger(PasswordResetService.class);

    private final SecureRandom random = new SecureRandom();
    private final AppUserRepository users;
    private final PasswordResetTokenRepository tokens;
    private final PasswordEncoder passwordEncoder;
    private final PortalSecurityProperties properties;
    private final Clock clock;

    public PasswordResetService(AppUserRepository users, PasswordResetTokenRepository tokens,
                                PasswordEncoder passwordEncoder, PortalSecurityProperties properties, Clock clock) {
        this.users = users;
        this.tokens = tokens;
        this.passwordEncoder = passwordEncoder;
        this.properties = properties;
        this.clock = clock;
    }

    /**
     * Returns the raw token (to be e-mailed), or empty if the e-mail is unknown or
     * the account is disabled. The CONTROLLER answers the same in both cases, so
     * the endpoint cannot be used to find out which e-mail addresses exist.
     */
    @Transactional
    public Optional<String> request(String email) {
        Optional<AppUser> user = users.findByEmail(email.toLowerCase(Locale.ROOT)).filter(AppUser::isEnabled);
        if (user.isEmpty()) {
            return Optional.empty();
        }
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        LocalDateTime now = LocalDateTime.now(clock);
        tokens.save(new PasswordResetToken(user.get(), sha256(token), now.plus(properties.resetTokenValidity()), now));
        log.info("Password reset requested for {} (in production the link is e-mailed)", user.get().getUsername());
        return Optional.of(token);
    }

    @Transactional
    public void confirm(String token, String newPassword) {
        LocalDateTime now = LocalDateTime.now(clock);
        PasswordResetToken reset = tokens.findByTokenHash(sha256(token))
                .filter(t -> t.isUsableAt(now))
                .orElseThrow(() -> ApiException.badRequest("Reset token is invalid, expired or already used"));
        reset.markUsed(now);
        reset.getUser().changePassword(passwordEncoder.encode(newPassword), now);   // also clears a lock
    }

    static String sha256(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is always available in Java", e);
        }
    }
}
