package com.example.bank.gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.mock.web.server.MockServerWebExchange;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;

class AuthenticationFilterTest {

    private static final String SECRET = "unit-test-secret-unit-test-secret-1234567890";

    private final GatewayFilter filter = new AuthenticationFilter(SECRET).apply(new AuthenticationFilter.Config());
    private final AtomicBoolean forwarded = new AtomicBoolean(false);
    private final GatewayFilterChain chain = exchange -> {
        forwarded.set(true);
        return Mono.empty();
    };

    private String token(String secret, long lifetimeMs) {
        return Jwts.builder().subject("mohit").expiration(new Date(System.currentTimeMillis() + lifetimeMs))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8))).compact();
    }

    private MockServerWebExchange call(MockServerHttpRequest.BaseBuilder<?> request) {
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        filter.filter(exchange, chain).block();
        return exchange;
    }

    @Test
    void validTokenIsForwarded() {
        MockServerWebExchange exchange = call(MockServerHttpRequest.get("/api/v1/customers")
                .header("Authorization", "Bearer " + token(SECRET, 60_000)));

        assertTrue(forwarded.get());
        assertEquals(null, exchange.getResponse().getStatusCode());
    }

    @Test
    void missingTokenGives401() {
        MockServerWebExchange exchange = call(MockServerHttpRequest.get("/api/v1/customers"));

        assertFalse(forwarded.get());
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
        assertEquals("Missing Bearer token", ((MockServerHttpResponse) exchange.getResponse()).getBodyAsString().block());
    }

    @Test
    void tokenSignedWithAnotherSecretGives401() {
        MockServerWebExchange exchange = call(MockServerHttpRequest.get("/api/v1/customers")
                .header("Authorization", "Bearer " + token("a-completely-different-secret-0123456789", 60_000)));

        assertFalse(forwarded.get());
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
        assertEquals("Invalid or expired token", ((MockServerHttpResponse) exchange.getResponse()).getBodyAsString().block());
    }

    @Test
    void expiredTokenGives401() {
        MockServerWebExchange exchange = call(MockServerHttpRequest.get("/api/v1/customers")
                .header("Authorization", "Bearer " + token(SECRET, -60_000)));

        assertFalse(forwarded.get());
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    void corsPreflightIsNotChecked() {
        call(MockServerHttpRequest.options("/api/v1/customers"));

        assertTrue(forwarded.get());
    }

    @Test
    void missingOrShortSecretStopsStartup() {
        assertThrows(IllegalStateException.class, () -> new AuthenticationFilter(""));
        assertThrows(IllegalStateException.class, () -> new AuthenticationFilter("too-short"));
    }

}
