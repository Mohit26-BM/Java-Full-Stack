package com.company.methodsec.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * BCrypt, strength 10 here (as in the roles-authorities project).
 *
 * With HTTP Basic the password travels with EVERY request, so the BCrypt check
 * also runs on every request: ~0.5 s at strength 12, ~0.14 s at 10. This project
 * sends hundreds of requests in its tests, so it uses 10, the Spring default.
 * (A token, as in the JWT project, avoids re-checking the password each time.)
 */
@Configuration
public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder(@Value("${portal.bcrypt-strength:10}") int strength) {
        return new BCryptPasswordEncoder(strength);
    }
}
