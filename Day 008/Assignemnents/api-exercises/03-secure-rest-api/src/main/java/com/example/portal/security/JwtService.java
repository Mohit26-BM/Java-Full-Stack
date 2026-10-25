package com.example.portal.security;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

/**
 * Creates and checks JSON Web Tokens.
 *
 * A JWT has three Base64 parts:  header.payload.signature
 *   header    {"alg":"HS256"}
 *   payload   the CLAIMS: iss (issuer), sub (username), iat / exp (issued / expires),
 *             roles, token_type ("access" or "refresh")
 *   signature HMAC-SHA256 of header + payload, made with the secret key.
 * Changing any character of the header or payload makes the signature wrong,
 * so a client cannot give itself more roles or a later expiry.
 *
 * Two kinds of token:
 *   access  - short-lived (15 min), sent with every API call
 *   refresh - longer-lived (24 h), only accepted by /api/auth/refresh
 */
@Component
public class JwtService {

    public static final String ACCESS = "access";
    public static final String REFRESH = "refresh";

    private final JwtEncoder encoder;
    private final JwtDecoder accessTokenDecoder;
    private final JwtDecoder refreshTokenDecoder;
    private final String issuer;
    private final long accessTokenValiditySeconds;
    private final long refreshTokenValiditySeconds;

    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.issuer}") String issuer,
                      @Value("${jwt.access-token-validity-seconds}") long accessTokenValiditySeconds,
                      @Value("${jwt.refresh-token-validity-seconds}") long refreshTokenValiditySeconds,
                      @Value("${jwt.clock-skew-seconds}") long clockSkewSeconds) {
        if (secret.length() < 32) {
            throw new IllegalStateException("jwt.secret must be at least 32 characters for HS256");
        }
        SecretKey key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        this.issuer = issuer;
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
        this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;

        this.encoder = new NimbusJwtEncoder(new ImmutableSecret<>(key));
        Duration clockSkew = Duration.ofSeconds(clockSkewSeconds);
        this.accessTokenDecoder = createDecoder(key, clockSkew, ACCESS);
        this.refreshTokenDecoder = createDecoder(key, clockSkew, REFRESH);
    }

    /**
     * A decoder checks, for every token:
     *   1. the signature (made with our secret key, algorithm HS256)
     *   2. exp / nbf    - not expired (JwtTimestampValidator)
     *   3. iss          - issued by us (JwtIssuerValidator)
     *   4. token_type   - the right kind of token
     */
    private JwtDecoder createDecoder(SecretKey key, Duration clockSkew, String expectedTokenType) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                new JwtTimestampValidator(clockSkew),
                new JwtIssuerValidator(issuer),
                new JwtClaimValidator<String>("token_type", expectedTokenType::equals)));
        return decoder;
    }

    public String createAccessToken(String username, List<String> roles) {
        return createToken(username, roles, ACCESS, accessTokenValiditySeconds);
    }

    public String createRefreshToken(String username) {
        return createToken(username, List.of(), REFRESH, refreshTokenValiditySeconds);
    }

    private String createToken(String username, List<String> roles, String tokenType, long validitySeconds) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject(username)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(validitySeconds))
                .claim("roles", roles)
                .claim("token_type", tokenType)
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    /** Used by Spring Security for every API request (see SecurityConfig). */
    public JwtDecoder getAccessTokenDecoder() {
        return accessTokenDecoder;
    }

    /** Checks a refresh token; throws JwtException if it is invalid, expired or not a refresh token. */
    public Jwt decodeRefreshToken(String token) {
        return refreshTokenDecoder.decode(token);
    }

    public long getAccessTokenValiditySeconds() {
        return accessTokenValiditySeconds;
    }
}
