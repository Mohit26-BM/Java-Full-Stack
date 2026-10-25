package com.company.secureapi.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.company.secureapi.dto.Dtos.CreateUserRequest;
import com.company.secureapi.dto.Dtos.LoginRequest;
import com.company.secureapi.dto.Dtos.TokenResponse;
import com.company.secureapi.dto.Dtos.UserResponse;
import com.company.secureapi.service.AuthAndUserService;

import jakarta.validation.Valid;

@RestController
public class AuthAndAdminController {
    private final AuthAndUserService service;

    public AuthAndAdminController(AuthAndUserService service) {
        this.service = service;
    }

    @PostMapping("/api/auth/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return service.login(request.username(), request.password());
    }

    @GetMapping("/api/admin/users")
    public List<UserResponse> users() {
        return service.findAll();
    }

    @PostMapping("/api/admin/users")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse created = service.create(request);
        return ResponseEntity.created(URI.create("/api/admin/users/" + created.id())).body(created);
    }
}
