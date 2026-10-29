package com.example.bank.account;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.sun.net.httpserver.HttpServer;

/** Runs against a tiny local HTTP server that plays the customer service. */
class CustomerClientTest {

    private final DiscoveryClient discovery = mock(DiscoveryClient.class);
    private final AtomicReference<String> receivedAuthorization = new AtomicReference<>();
    private final AtomicInteger slowCalls = new AtomicInteger();
    private final AtomicInteger flakyCalls = new AtomicInteger();
    private HttpServer server;
    private CustomerClient client;

    @BeforeEach
    void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.setExecutor(Executors.newCachedThreadPool()); // a sleeping handler must not block the next request
        server.createContext("/api/v1/customers/1", exchange -> {
            receivedAuthorization.set(exchange.getRequestHeaders().getFirst("Authorization"));
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
        });
        server.createContext("/api/v1/customers/99", exchange -> {
            exchange.sendResponseHeaders(404, -1);
            exchange.close();
        });
        // never answers in time
        server.createContext("/api/v1/customers/2", exchange -> {
            slowCalls.incrementAndGet();
            sleep(800);
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
        });
        // the first call is too slow, the second is fine
        server.createContext("/api/v1/customers/3", exchange -> {
            if (flakyCalls.incrementAndGet() == 1) {
                sleep(800);
            }
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
        });
        server.start();
        client = new CustomerClient(discovery, Duration.ofMillis(500), Duration.ofMillis(200));
    }

    @AfterEach
    void tearDown() {
        server.stop(0);
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    private void customerServiceRegisteredOnTheLocalServer() {
        when(discovery.getInstances("CUSTOMER")).thenReturn(List.of(
                new DefaultServiceInstance("customer-1", "CUSTOMER", "localhost", server.getAddress().getPort(), false)));
    }

    @Test
    void existingCustomerPassesAndTheTokenIsForwarded() {
        customerServiceRegisteredOnTheLocalServer();

        assertDoesNotThrow(() -> client.requireCustomer(1L, "Bearer my-token"));

        assertEquals("Bearer my-token", receivedAuthorization.get());
    }

    @Test
    void unknownCustomerGives404AndIsNotRetried() {
        customerServiceRegisteredOnTheLocalServer();

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> client.requireCustomer(99L, "Bearer my-token"));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("Customer 99 does not exist", ex.getReason());
    }

    @Test
    void noRegisteredInstanceGives503() {
        when(discovery.getInstances("CUSTOMER")).thenReturn(List.of());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> client.requireCustomer(1L, "Bearer my-token"));

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, ex.getStatusCode());
    }

    @Test
    void aServiceThatNeverAnswersTimesOutAfterTwoAttemptsInsteadOfHanging() {
        customerServiceRegisteredOnTheLocalServer();

        long start = System.nanoTime();
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> client.requireCustomer(2L, "Bearer my-token"));
        long elapsedMs = (System.nanoTime() - start) / 1_000_000;

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, ex.getStatusCode());
        assertEquals(2, slowCalls.get());
        assertEquals(true, elapsedMs < 2_000, "gave up after " + elapsedMs + " ms");
    }

    @Test
    void aSlowFirstAttemptIsRetriedAndTheSecondSucceeds() {
        customerServiceRegisteredOnTheLocalServer();

        assertDoesNotThrow(() -> client.requireCustomer(3L, "Bearer my-token"));

        assertEquals(2, flakyCalls.get());
    }

}
