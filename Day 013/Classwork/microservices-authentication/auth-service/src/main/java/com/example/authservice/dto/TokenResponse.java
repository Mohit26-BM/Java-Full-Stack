package com.example.authservice.dto;

public record TokenResponse(String token, String type, long expiresInSeconds) {
}
