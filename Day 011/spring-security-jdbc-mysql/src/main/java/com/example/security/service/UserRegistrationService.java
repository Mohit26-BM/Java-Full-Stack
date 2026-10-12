package com.example.security.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserRegistrationService {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    public UserRegistrationService(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(String username, String password) {

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE username = ?",
                Integer.class,
                username
        );

        if (count != null && count > 0) {
            throw new RuntimeException("Username already exists");
        }

        jdbcTemplate.update(
                "INSERT INTO users(username, password, enabled) VALUES (?, ?, ?)",
                username,
                passwordEncoder.encode(password),
                true
        );

        jdbcTemplate.update(
                "INSERT INTO authorities(username, authority) VALUES (?, ?)",
                username,
                "ROLE_USER"
        );
    }
}