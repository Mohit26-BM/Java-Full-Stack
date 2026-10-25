package com.example.hospital.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Switches on Spring Data JPA auditing.
 * Entities that use AuditingEntityListener get their @CreatedDate and
 * @LastModifiedDate fields filled in automatically on INSERT and UPDATE.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
