package com.company.dbauth.security;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.company.dbauth.config.PortalSecurityProperties;
import com.company.dbauth.repository.AppUserRepository;

/**
 * Account locking.
 *
 * Spring Security PUBLISHES an event after every authentication attempt
 * (DefaultAuthenticationEventPublisher, registered by Spring Boot). This
 * listener reacts to them:
 *
 *   wrong password -> failed_login_attempts + 1; at the limit, locked_until = now + lock duration
 *   success        -> the counter goes back to 0
 *
 * While locked_until is in the future the principal reports
 * isAccountNonLocked() = false, so even the CORRECT password is refused. The
 * lock ends by itself; an admin (POST /api/admin/users/{id}/unlock) or a
 * password reset can end it early.
 */
@Component
public class LoginAttemptListener {

    private static final Logger log = LoggerFactory.getLogger(LoginAttemptListener.class);

    private final AppUserRepository users;
    private final PortalSecurityProperties properties;
    private final Clock clock;

    public LoginAttemptListener(AppUserRepository users, PortalSecurityProperties properties, Clock clock) {
        this.users = users;
        this.properties = properties;
        this.clock = clock;
    }

    @EventListener
    @Transactional
    public void onBadCredentials(AuthenticationFailureBadCredentialsEvent event) {
        // unknown usernames also end here (as "bad credentials"); there is nothing to count for them
        users.findByUsername(event.getAuthentication().getName().toLowerCase(Locale.ROOT)).ifPresent(user -> {
            LocalDateTime now = LocalDateTime.now(clock);
            user.recordFailedLogin(properties.maxFailedAttempts(), now.plus(properties.lockDuration()));
            if (user.isLockedAt(now)) {
                log.warn("Account {} locked until {} after {} failed logins",
                        user.getUsername(), user.getLockedUntil(), user.getFailedLoginAttempts());
            }
        });
    }

    @EventListener
    @Transactional
    public void onSuccess(AuthenticationSuccessEvent event) {
        users.findByUsername(event.getAuthentication().getName()).ifPresent(user -> {
            if (user.getFailedLoginAttempts() > 0) {      // HTTP Basic logs in on every request: write only when needed
                user.clearFailedLogins();
            }
        });
    }
}
