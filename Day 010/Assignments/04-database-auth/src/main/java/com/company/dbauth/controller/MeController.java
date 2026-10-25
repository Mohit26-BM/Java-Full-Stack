package com.company.dbauth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.dbauth.dto.AuthRequests.PasswordChangeRequest;
import com.company.dbauth.dto.UserResponses.MeResponse;
import com.company.dbauth.security.PortalUserPrincipal;
import com.company.dbauth.service.AccountService;

import jakarta.validation.Valid;

/**
 * The logged-in user. @AuthenticationPrincipal injects the PortalUserPrincipal
 * that DatabaseUserDetailsService built for this request.
 */
@RestController
@RequestMapping("/api/me")
public class MeController {

    private final AccountService accountService;

    public MeController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public MeResponse me(@AuthenticationPrincipal PortalUserPrincipal principal) {
        return new MeResponse(principal.getUsername(), principal.getFullName(),
                principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
    }

    @PostMapping("/password")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal PortalUserPrincipal principal,
                                               @Valid @RequestBody PasswordChangeRequest request) {
        accountService.changePassword(principal.getUsername(), request);
        return ResponseEntity.noContent().build();
    }
}
