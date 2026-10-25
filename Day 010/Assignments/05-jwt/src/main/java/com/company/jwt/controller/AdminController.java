package com.company.jwt.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.jwt.dto.AuthDtos.EnabledRequest;
import com.company.jwt.dto.AuthDtos.UserSummary;
import com.company.jwt.security.AuthenticatedUser;
import com.company.jwt.service.UserAdminService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/users")
public class AdminController {
    private final UserAdminService userAdminService;

    public AdminController(UserAdminService userAdminService) {
        this.userAdminService = userAdminService;
    }

    @GetMapping
    public List<UserSummary> findAll() {
        return userAdminService.findAll();
    }

    @PatchMapping("/{id}/enabled")
    public UserSummary setEnabled(@PathVariable("id") Long id, @Valid @RequestBody EnabledRequest request,
                                  @AuthenticationPrincipal AuthenticatedUser principal) {
        return userAdminService.setEnabled(id, request.enabled(), principal.username());
    }

    @PostMapping("/{id}/revoke-tokens")
    public Map<String, Integer> revokeTokens(@PathVariable("id") Long id) {
        return Map.of("revokedRefreshTokens", userAdminService.revokeTokens(id));
    }
}
