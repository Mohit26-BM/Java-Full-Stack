package com.company.security.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

@Validated
@ConfigurationProperties(prefix = "portal")
public record PortalUsersProperties(@NotEmpty List<@Valid UserEntry> users) {
    public record UserEntry(
            @NotBlank String username,
            @Pattern(regexp = "^\\$2[aby]\\$\\d{2}\\$.{53}$", message = "must be a BCrypt hash, not a plain password")
            String passwordHash,
            @NotEmpty List<String> roles) {
    }
}
