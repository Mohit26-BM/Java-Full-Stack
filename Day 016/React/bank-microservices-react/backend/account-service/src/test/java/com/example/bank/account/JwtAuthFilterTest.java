package com.example.bank.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

class JwtAuthFilterTest {

    private static final String SECRET = "unit-test-secret-unit-test-secret-1234567890";

    private final JwtAuthFilter filter = new JwtAuthFilter(SECRET);

    private String token(String secret) {
        return Jwts.builder().subject("mohit").expiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8))).compact();
    }

    private MockHttpServletResponse run(MockHttpServletRequest request, MockFilterChain chain) throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, chain);
        return response;
    }

    @Test
    void requestWithoutATokenIsRejected() throws Exception {
        MockFilterChain chain = new MockFilterChain();

        MockHttpServletResponse response = run(new MockHttpServletRequest("GET", "/api/v1/accounts"), chain);

        assertEquals(401, response.getStatus());
        assertEquals("Missing Bearer token", response.getContentAsString());
        assertNull(chain.getRequest());
    }

    @Test
    void tokenSignedWithAnotherSecretIsRejected() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/accounts");
        request.addHeader("Authorization", "Bearer " + token("a-completely-different-secret-0123456789"));
        MockFilterChain chain = new MockFilterChain();

        MockHttpServletResponse response = run(request, chain);

        assertEquals(401, response.getStatus());
        assertNull(chain.getRequest());
    }

    @Test
    void validTokenPassesThrough() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/accounts");
        request.addHeader("Authorization", "Bearer " + token(SECRET));
        MockFilterChain chain = new MockFilterChain();

        MockHttpServletResponse response = run(request, chain);

        assertEquals(200, response.getStatus());
        assertNotNull(chain.getRequest());
    }

    @Test
    void theUsernameFromTheTokenIsPassedOnForTheLedger() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/accounts");
        request.addHeader("Authorization", "Bearer " + token(SECRET));

        run(request, new MockFilterChain());

        assertEquals("mohit", request.getAttribute("username"));
    }

    @Test
    void missingOrShortSecretStopsStartup() {
        assertThrows(IllegalStateException.class, () -> new JwtAuthFilter(""));
        assertThrows(IllegalStateException.class, () -> new JwtAuthFilter("too-short"));
    }

}
