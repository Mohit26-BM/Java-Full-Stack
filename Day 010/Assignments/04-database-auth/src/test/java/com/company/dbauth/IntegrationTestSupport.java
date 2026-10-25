package com.company.dbauth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.company.dbauth.repository.AppUserRepository;

/**
 * Base class: the full application against the MySQL TEST database.
 * Tests that change an account first register a FRESH user (random name), so
 * they never depend on each other or on the order they run in.
 */
@SpringBootTest
@AutoConfigureMockMvc
abstract class IntegrationTestSupport {

    static final String PASSWORD = "Start@123";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AppUserRepository users;

    record TestAccount(Long id, String username, String email, String password) {
    }

    /** Registers a new USER through the real endpoint and returns its credentials. */
    TestAccount registerFreshUser() throws Exception {
        String username = "t" + UUID.randomUUID().toString().substring(0, 8);
        String email = username + "@company.com";
        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content("""
                        {"username":"%s","email":"%s","fullName":"Test User","password":"%s"}"""
                        .formatted(username, email, PASSWORD)))
                .andExpect(status().isCreated());
        return new TestAccount(users.findByUsername(username).orElseThrow().getId(), username, email, PASSWORD);
    }
}
