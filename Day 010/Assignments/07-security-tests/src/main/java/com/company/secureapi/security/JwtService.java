package com.company.secureapi.security;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

@Service
public class JwtService {
    private final SecretKey key;
    private final String issuer;
    private final Duration ttl;
    private final JwtParser parser;

    public JwtService(@Value("${portal.jwt.secret}") String secret,
                      @Value("${portal.jwt.issuer}") String issuer,
                      @Value("${portal.jwt.access-token-ttl}") Duration ttl) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.issuer = issuer;
        this.ttl = ttl;
        this.parser = Jwts.parser().verifyWith(key).requireIssuer(issuer).build();
    }

    public long ttlSeconds() {
        return ttl.toSeconds();
    }

    public String issue(String username, Long employeeId, List<String> authorities) {
        Instant now = Instant.now();
        return Jwts.builder()
                .issuer(issuer).subject(username).id(UUID.randomUUID().toString())
                .issuedAt(Date.from(now)).expiration(Date.from(now.plus(ttl)))
                .claim("emp", employeeId)
                .claim("authorities", authorities)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public Claims verify(String token) {
        try {
            return parser.parseSignedClaims(token).getPayload();
        } catch (ExpiredJwtException e) {
            throw new InvalidTokenException("Token has expired");
        } catch (SignatureException e) {
            throw new InvalidTokenException("Token signature is invalid");
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException("Token is malformed or invalid");
        }
    }
}
