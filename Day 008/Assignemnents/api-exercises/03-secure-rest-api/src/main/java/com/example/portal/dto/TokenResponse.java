package com.example.portal.dto;

import java.util.List;

/**
 * Returned by login and refresh.
 * accessToken  - send it on every API call:  Authorization: Bearer <accessToken>
 * expiresIn    - seconds until the access token expires
 * refreshToken - send it to /api/auth/refresh to get a new access token
 */
public record TokenResponse(String accessToken,
                            String tokenType,
                            long expiresIn,
                            String refreshToken,
                            String username,
                            List<String> roles) {
}
