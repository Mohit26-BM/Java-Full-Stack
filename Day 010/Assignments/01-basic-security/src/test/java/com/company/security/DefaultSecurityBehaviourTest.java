package com.company.security;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * The "defaults" profile switches OUR SecurityConfig off, leaving Spring Boot's
 * default security: EVERY endpoint is protected (even /api/public/hello), a
 * login page is generated, and one user "user" with a random password is
 * printed in the console at startup.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("defaults")
class DefaultSecurityBehaviourTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void byDefault_evenThePublicEndpointIsProtected() throws Exception {
        mockMvc.perform(get("/api/public/hello"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void byDefault_springGeneratesALoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Please sign in")));
    }
}
