package com.example.portal.service;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import com.example.portal.dto.LoginRequest;
import com.example.portal.dto.TokenResponse;
import com.example.portal.exception.InvalidTokenException;
import com.example.portal.security.JwtService;

/** Login (username + password -> tokens) and refresh (refresh token -> new access token). */
@Service
public class AuthService {

    private static final Logger audit = LoggerFactory.getLogger("AUDIT");

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager, UserDetailsService userDetailsService,
                       JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    /**
     * Checks the password (BCrypt) through Spring Security's AuthenticationManager.
     * Wrong credentials -> BadCredentialsException -> 401 (GlobalExceptionHandler).
     */
    public TokenResponse login(LoginRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        } catch (AuthenticationException ex) {
            audit.warn("user={} action=LOGIN result=FAILED", request.username());
            throw ex;
        }
        List<String> roles = toRoleNames(authentication.getAuthorities());
        audit.info("user={} action=LOGIN result=SUCCESS roles={}", authentication.getName(), roles);
        return buildResponse(authentication.getName(), roles, jwtService.createRefreshToken(authentication.getName()));
    }

    /**
     * Issues a new ACCESS token for a valid REFRESH token. The user's roles are
     * loaded again, so a role change takes effect at the next refresh.
     */
    public TokenResponse refresh(String refreshToken) {
        Jwt jwt;
        try {
            jwt = jwtService.decodeRefreshToken(refreshToken);
        } catch (JwtException ex) {
            throw new InvalidTokenException("Invalid or expired refresh token: " + ex.getMessage());
        }
        UserDetails user;
        try {
            user = userDetailsService.loadUserByUsername(jwt.getSubject());
        } catch (UsernameNotFoundException ex) {
            throw new InvalidTokenException("The user of this refresh token no longer exists");
        }
        List<String> roles = toRoleNames(user.getAuthorities());
        return buildResponse(user.getUsername(), roles, refreshToken);
    }

    private TokenResponse buildResponse(String username, List<String> roles, String refreshToken) {
        String accessToken = jwtService.createAccessToken(username, roles);
        return new TokenResponse(accessToken, "Bearer", jwtService.getAccessTokenValiditySeconds(),
                refreshToken, username, roles);
    }

    /** ROLE_HR -> HR (the token stores plain role names). */
    private List<String> toRoleNames(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .map(authority -> authority.replace("ROLE_", ""))
                .toList();
    }
}
