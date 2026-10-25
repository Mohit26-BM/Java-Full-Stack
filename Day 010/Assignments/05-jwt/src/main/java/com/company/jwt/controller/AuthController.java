package com.company.jwt.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.jwt.dto.AuthDtos.LoginRequest;
import com.company.jwt.dto.AuthDtos.RefreshRequest;
import com.company.jwt.dto.AuthDtos.TokenResponse;
import com.company.jwt.security.AuthenticatedUser;
import com.company.jwt.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request.username(), request.password());
    }

    @PostMapping("/refresh")
    public TokenResponse refresh(@Valid @RequestBody RefreshRequest request) {
        return authService.refresh(request.refreshToken());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal AuthenticatedUser principal,
                                       @RequestBody(required = false) RefreshRequest request) {
        authService.logout(principal, request == null ? null : request.refreshToken());
        return ResponseEntity.noContent().build();
    }
}
