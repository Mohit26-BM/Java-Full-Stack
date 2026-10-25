package com.example.portal.dto;

import jakarta.validation.constraints.NotBlank;

/** Body of POST /api/auth/refresh */
public record RefreshRequest(

        @NotBlank(message = "refreshToken is required")
        String refreshToken) {
}
