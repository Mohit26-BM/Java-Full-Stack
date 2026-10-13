package com.example.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(

        @NotBlank(message = "Username is required")
        @Pattern(regexp = "^[A-Za-z0-9_]{3,30}$",
                message = "Username must be 3-30 letters, digits or underscores")
        String username,

        @NotBlank(message = "Password is required")
        @Pattern(regexp = "^[\\x20-\\x7E]{8,72}$",
                message = "Password must be 8-72 printable ASCII characters")
        String password) {
}
