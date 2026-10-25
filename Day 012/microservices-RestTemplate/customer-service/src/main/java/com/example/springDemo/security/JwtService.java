package com.example.springDemo.security;

import java.time.Duration;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.example.springDemo.entity.UserAccount;

@Service
public class JwtService {

    private final JwtEncoder encoder;
    private final long expiryMinutes;

    public JwtService(JwtEncoder encoder, @Value("${app.jwt.expiry-minutes:480}") long expiryMinutes) {
        this.encoder = encoder;
        this.expiryMinutes = expiryMinutes;
    }

    public String issue(UserAccount user) {
        Instant now = Instant.now();
        JwtClaimsSet.Builder claims = JwtClaimsSet.builder()
                .subject(user.getEmail())
                .issuedAt(now)
                .expiresAt(now.plus(Duration.ofMinutes(expiryMinutes)))
                .claim("role", user.getRole().name());
        if (user.getCustomerId() != null) {
            claims.claim("customerId", user.getCustomerId());
        }
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(header, claims.build())).getTokenValue();
    }
}
