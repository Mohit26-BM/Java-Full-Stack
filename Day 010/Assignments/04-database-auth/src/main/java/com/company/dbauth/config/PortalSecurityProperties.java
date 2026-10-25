package com.company.dbauth.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Validated
@ConfigurationProperties(prefix = "portal.security")
public record PortalSecurityProperties(
        @Min(4) @Max(31) int bcryptStrength,
        @Min(1) int maxFailedAttempts,
        @NotNull Duration lockDuration,
        @NotNull Duration resetTokenValidity,
        boolean returnResetToken) {
}
