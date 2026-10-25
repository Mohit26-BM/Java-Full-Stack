package com.company.jwt.dto;

import java.time.Instant;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public final class AuthDtos {
    private AuthDtos() {
    }

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {
        @Override
        public String toString() {
            return "LoginRequest[" + username + ", ***]";
        }
    }

    public record RefreshRequest(@NotBlank String refreshToken) {
        @Override
        public String toString() {
            return "RefreshRequest[***]";
        }
    }

    public record TokenResponse(String tokenType, String accessToken, long expiresIn,
                                String refreshToken, long refreshExpiresIn) {
    }

    public record MeResponse(String username, List<String> authorities, String tokenId, Instant tokenExpiresAt) {
    }

    public record EnabledRequest(@NotNull Boolean enabled) {
    }

    public record UserSummary(Long id, String username, String fullName, boolean enabled, List<String> roles) {
    }

    public record DirectoryEntry(String fullName, String email) {
    }
}
