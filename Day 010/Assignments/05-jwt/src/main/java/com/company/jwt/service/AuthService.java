package com.company.jwt.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.jwt.config.JwtProperties;
import com.company.jwt.dto.AuthDtos.TokenResponse;
import com.company.jwt.entity.AppUser;
import com.company.jwt.exception.ApiException;
import com.company.jwt.repository.AppUserRepository;
import com.company.jwt.security.AuthenticatedUser;
import com.company.jwt.security.DatabaseUserDetailsService;
import com.company.jwt.security.JwtService;
import com.company.jwt.security.JwtService.IssuedToken;
import com.company.jwt.security.RevokedAccessTokens;
import com.company.jwt.service.RefreshTokenService.Rotation;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokens;
    private final RevokedAccessTokens revokedAccessTokens;
    private final AppUserRepository users;
    private final JwtProperties properties;

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService,
                       RefreshTokenService refreshTokens, RevokedAccessTokens revokedAccessTokens,
                       AppUserRepository users, JwtProperties properties) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokens = refreshTokens;
        this.revokedAccessTokens = revokedAccessTokens;
        this.users = users;
        this.properties = properties;
    }

    @Transactional
    public TokenResponse login(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(username, password));

        AppUser user = users.findWithAuthoritiesByUsername(authentication.getName()).orElseThrow();
        IssuedToken access = jwtService.issueAccessToken(authentication.getName(),
                authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
        return response(access, refreshTokens.issue(user));
    }

    @Transactional(noRollbackFor = ApiException.class)
    public TokenResponse refresh(String rawRefreshToken) {
        Rotation rotation = refreshTokens.rotate(rawRefreshToken);
        AppUser user = users.findWithAuthoritiesById(rotation.user().getId()).orElseThrow();
        if (!user.isEnabled()) {
            refreshTokens.revokeAll(user.getId());
            throw ApiException.unauthorized("Account is disabled");
        }
        IssuedToken access = jwtService.issueAccessToken(user.getUsername(),
                DatabaseUserDetailsService.authoritiesOf(user));
        return response(access, rotation.newRawToken());
    }

    @Transactional
    public void logout(AuthenticatedUser principal, String rawRefreshToken) {
        if (rawRefreshToken != null && !rawRefreshToken.isBlank()) {
            refreshTokens.revokeSession(rawRefreshToken, principal.username());
        }
        revokedAccessTokens.revoke(principal.tokenId(), principal.tokenExpiresAt());
    }

    private TokenResponse response(IssuedToken access, String refreshToken) {
        return new TokenResponse("Bearer", access.value(), properties.accessTokenTtl().toSeconds(),
                refreshToken, properties.refreshTokenTtl().toSeconds());
    }
}
