package com.example.authservice.service;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtService {

    private final Key signKey;
    private final long expiryMillis;

    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.expiry-minutes}") long expiryMinutes) {
        this.signKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.expiryMillis = expiryMinutes * 60 * 1000;
    }

    public void validateToken(final String token) {
        try {
            Jwts.parserBuilder().setSigningKey(signKey).build().parseClaimsJws(token);
        } catch (IllegalArgumentException ex) {
            throw new JwtException("Token is empty", ex);
        }
    }

    public String generateToken(String userName) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(userName)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expiryMillis))
                .signWith(signKey, SignatureAlgorithm.HS256)
                .compact();
    }
}
