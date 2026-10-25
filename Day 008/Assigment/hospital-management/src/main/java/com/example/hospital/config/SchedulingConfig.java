package com.example.hospital.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Switches on @Scheduled methods (the NO_SHOW job), unless
 * hospital.no-show.enabled=false. The tests switch it off and call the job
 * directly with a fixed time instead.
 */
@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "hospital.no-show.enabled", havingValue = "true", matchIfMissing = true)
public class SchedulingConfig {
}
