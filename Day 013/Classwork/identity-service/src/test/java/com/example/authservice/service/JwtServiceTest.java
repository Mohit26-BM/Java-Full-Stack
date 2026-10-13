package com.example.authservice.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import io.jsonwebtoken.JwtException;

class JwtServiceTest {

    private static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";
    private static final String OTHER_SECRET = "YS1kaWZmZXJlbnQtc2VjcmV0LWtleS1mb3ItdGhlLXRhbXBlci10ZXN0LTAxMjM0NTY3ODk=";

    private final JwtService jwtService = new JwtService(SECRET, 30);

    @Test
    void validTokenIsAccepted() {
        String token = jwtService.generateToken("mohit");
        assertDoesNotThrow(() -> jwtService.validateToken(token));
    }

    @Test
    void tokenSignedWithAnotherKeyIsRejected() {
        String token = new JwtService(OTHER_SECRET, 30).generateToken("mohit");
        assertThrows(JwtException.class, () -> jwtService.validateToken(token));
    }

    @Test
    void expiredTokenIsRejected() {
        String token = new JwtService(SECRET, -1).generateToken("mohit");
        assertThrows(JwtException.class, () -> jwtService.validateToken(token));
    }

    @Test
    void garbageAndEmptyTokensAreRejected() {
        assertThrows(JwtException.class, () -> jwtService.validateToken("not-a-token"));
        assertThrows(JwtException.class, () -> jwtService.validateToken(""));
    }
}
