package com.company.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("!defaults")
public class PasswordConfig {
    @Bean
    public PasswordEncoder passwordEncoder(@Value("${portal.bcrypt-strength:12}") int strength) {
        return new BCryptPasswordEncoder(strength);
    }
}
