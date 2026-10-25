package com.company.dbauth.controller;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.company.dbauth.config.PortalSecurityProperties;
import com.company.dbauth.dto.AuthRequests.RegistrationRequest;
import com.company.dbauth.dto.AuthRequests.ResetConfirmRequest;
import com.company.dbauth.dto.AuthRequests.ResetRequest;
import com.company.dbauth.dto.UserResponses.ResetRequested;
import com.company.dbauth.dto.UserResponses.UserResponse;
import com.company.dbauth.service.AccountService;
import com.company.dbauth.service.PasswordResetService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final String RESET_MESSAGE =
            "If an active account uses this e-mail address, a reset link has been sent";

    private final AccountService accountService;
    private final PasswordResetService passwordResetService;
    private final PortalSecurityProperties properties;

    public AuthController(AccountService accountService, PasswordResetService passwordResetService,
                          PortalSecurityProperties properties) {
        this.accountService = accountService;
        this.passwordResetService = passwordResetService;
        this.properties = properties;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegistrationRequest request) {
        UserResponse created = accountService.register(request);
        return ResponseEntity.created(URI.create("/api/admin/users/" + created.id())).body(created);
    }

    @PostMapping("/password-reset/request")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResetRequested requestReset(@Valid @RequestBody ResetRequest request) {
        String token = passwordResetService.request(request.email())
                .filter(t -> properties.returnResetToken())
                .orElse(null);
        return new ResetRequested(RESET_MESSAGE, token);
    }

    @PostMapping("/password-reset/confirm")
    public ResponseEntity<Void> confirmReset(@Valid @RequestBody ResetConfirmRequest request) {
        passwordResetService.confirm(request.token(), request.newPassword());
        return ResponseEntity.noContent().build();
    }
}
