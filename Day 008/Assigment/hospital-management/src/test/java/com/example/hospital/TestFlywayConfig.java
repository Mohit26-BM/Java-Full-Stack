package com.example.hospital;

import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Test-only: before the application starts, drop everything in the test
 * database (Flyway "clean") and run all migrations again. Every test run
 * therefore checks that the migrations build a working schema from nothing.
 *
 * Flyway refuses to clean unless spring.flyway.clean-disabled=false, which is
 * set only in the test application.properties - never for the real database.
 */
@Configuration
public class TestFlywayConfig {

    @Bean
    public FlywayMigrationStrategy cleanThenMigrate() {
        return flyway -> {
            flyway.clean();
            flyway.migrate();
        };
    }
}
