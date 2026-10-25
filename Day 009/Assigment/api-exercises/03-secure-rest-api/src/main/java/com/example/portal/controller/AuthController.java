package com.example.portal.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.portal.dto.LoginRequest;
import com.example.portal.dto.RefreshRequest;
import com.example.portal.dto.TokenResponse;
import com.example.portal.service.AuthService;

import jakarta.validation.Valid;

/** Public endpoints (no token needed) that hand out tokens. */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /** POST /api/auth/login  {"username":"hr","password":"hr123"} -> access + refresh token */
    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    /** POST /api/auth/refresh  {"refreshToken":"..."} -> new access token */
    @PostMapping("/refresh")
    public TokenResponse refresh(@Valid @RequestBody RefreshRequest request) {
        return authService.refresh(request.refreshToken());
    }
}
