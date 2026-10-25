package com.company.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Passwords are HASHED with BCrypt, never stored in plain text and never encrypted.
 *
 * - Hashing is one-way: there is no key and no way back to the password. To log
 *   in, the typed password is hashed again and the two hashes are compared.
 * - BCrypt adds a random SALT to every hash (the same password gives a different
 *   hash each time), so precomputed "rainbow tables" do not work.
 * - BCrypt is deliberately SLOW. The strength (cost) is the log2 of the number of
 *   rounds: every +1 doubles the work, for us once per login and for an attacker
 *   for every one of billions of guesses.
 *
 * The cost is stored inside each hash ($2a$12$...), so existing hashes keep
 * working if the strength for NEW hashes is changed later.
 *
 * Not active in the "defaults" profile: Spring Boot's generated default user has
 * a raw password, which only works without a custom PasswordEncoder bean.
 */
@Configuration
@Profile("!defaults")
public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder(@Value("${portal.bcrypt-strength:12}") int strength) {
        return new BCryptPasswordEncoder(strength);
    }
}
