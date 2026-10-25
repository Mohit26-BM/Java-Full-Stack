package com.company.jwt.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.jwt.dto.AuthDtos.MeResponse;
import com.company.jwt.security.AuthenticatedUser;

@RestController
public class MeController {
    @GetMapping("/api/me")
    public MeResponse me(@AuthenticationPrincipal AuthenticatedUser principal, Authentication authentication) {
        return new MeResponse(principal.username(),
                authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList(),
                principal.tokenId(), principal.tokenExpiresAt());
    }
}
