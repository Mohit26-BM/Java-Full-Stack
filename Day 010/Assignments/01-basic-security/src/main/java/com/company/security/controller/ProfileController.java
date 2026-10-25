package com.company.security.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * GET /api/profile - who am I? (HTTP Basic)
 *
 * Spring MVC injects the Authentication that the security filters stored in the
 * SecurityContext. By the time this method runs, authentication is already done.
 */
@RestController
public class ProfileController {

    @GetMapping("/api/profile")
    public Map<String, Object> profile(Authentication authentication) {
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .sorted()
                .toList();
        return Map.of(
                "username", authentication.getName(),
                "authorities", authorities,
                "authenticationType", authentication.getClass().getSimpleName());
    }
}
