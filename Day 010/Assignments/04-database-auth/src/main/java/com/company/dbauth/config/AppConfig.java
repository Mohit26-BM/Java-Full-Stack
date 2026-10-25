package com.company.dbauth.config;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

    /**
     * BCrypt for every password: registration, password change and reset all
     * hash with this bean, and the login compares with it. Strength 10, because
     * HTTP Basic re-checks the hash on every request.
     */
    @Bean
    public PasswordEncoder passwordEncoder(PortalSecurityProperties properties) {
        return new BCryptPasswordEncoder(properties.bcryptStrength());
    }

    /** "Now" comes from this bean (lock expiry, token expiry), so tests can control time. */
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
