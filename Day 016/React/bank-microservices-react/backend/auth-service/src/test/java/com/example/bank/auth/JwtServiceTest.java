package com.example.bank.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

class JwtServiceTest {

    private static final String SECRET = "unit-test-secret-unit-test-secret-1234567890";

    @Test
    void generatedTokenCanBeParsedAndHoldsTheUsername() {
        JwtService jwt = new JwtService(SECRET, 60_000);

        String token = jwt.generate("mohit", Role.TELLER, null);

        assertEquals("mohit", jwt.parse(token).getSubject());
    }

    @Test
    void tokenCarriesTheRoleAndTheLinkedCustomerId() {
        JwtService jwt = new JwtService(SECRET, 60_000);

        Claims customer = jwt.parse(jwt.generate("aarav", Role.CUSTOMER, 7L));

        assertEquals("CUSTOMER", customer.get("role", String.class));
        assertEquals(7L, customer.get("customerId", Number.class).longValue());
    }

    @Test
    void staffTokensHaveNoCustomerId() {
        JwtService jwt = new JwtService(SECRET, 60_000);

        Claims admin = jwt.parse(jwt.generate("demo", Role.ADMIN, null));

        assertEquals("ADMIN", admin.get("role", String.class));
        assertNull(admin.get("customerId"));
    }

    @Test
    void tokenSignedWithAnotherSecretIsRejected() {
        String token = new JwtService("a-completely-different-secret-0123456789", 60_000).generate("mohit", Role.ADMIN, null);

        assertThrows(JwtException.class, () -> new JwtService(SECRET, 60_000).parse(token));
    }

    @Test
    void expiredTokenIsRejected() {
        JwtService jwt = new JwtService(SECRET, -1_000);

        String token = jwt.generate("mohit", Role.CUSTOMER, 1L);

        assertThrows(ExpiredJwtException.class, () -> jwt.parse(token));
    }

    @Test
    void missingOrShortSecretStopsStartup() {
        assertThrows(IllegalStateException.class, () -> new JwtService("", 60_000));
        assertThrows(IllegalStateException.class, () -> new JwtService("too-short", 60_000));
    }

}
