package com.example.hospital.config;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The current date and time come from this Clock bean instead of calling
 * LocalDate.now() directly. In production it is the system clock; a test can
 * replace it with Clock.fixed(...) to check time-dependent rules (past slots,
 * the NO_SHOW job) at an exact moment.
 */
@Configuration
public class ClockConfig {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
