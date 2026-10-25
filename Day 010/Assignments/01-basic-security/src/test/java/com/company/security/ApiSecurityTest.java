package com.company.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/** The REST chain (/api/**): public vs protected, HTTP Basic, JSON 401, stateless, no CSRF. */
@SpringBootTest
@AutoConfigureMockMvc
class ApiSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void publicEndpoint_needsNoLogin() throws Exception {
        mockMvc.perform(get("/api/public/hello"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void protectedEndpoint_withoutCredentials_returns401Json() throws Exception {
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("WWW-Authenticate", "Basic"))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication is required to access this resource"))
                .andExpect(jsonPath("$.path").value("/api/employees"));
    }

    @Test
    void protectedEndpoint_withValidBasicCredentials_returnsData() throws Exception {
        mockMvc.perform(get("/api/employees").with(httpBasic("alice", "Alice@123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4));
    }

    @Test
    void wrongPassword_returns401_withTheSameMessageAsAnUnknownUser() throws Exception {
        mockMvc.perform(get("/api/employees").with(httpBasic("alice", "wrong")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid username or password"));

        mockMvc.perform(get("/api/employees").with(httpBasic("mallory", "Alice@123")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    /** The permitAll rule for /api/employees/count is listed BEFORE /api/employees/**, so it wins. */
    @Test
    void matcherOrder_specificPublicRuleBeforeTheGeneralRule() throws Exception {
        mockMvc.perform(get("/api/employees/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employees").value(4));
        mockMvc.perform(get("/api/employees/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void profile_showsTheAuthenticatedUserAndRoles() throws Exception {
        mockMvc.perform(get("/api/profile").with(httpBasic("admin", "Admin@123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.authorities[0]").value("ROLE_ADMIN"))
                .andExpect(jsonPath("$.authorities[1]").value("ROLE_USER"))
                .andExpect(jsonPath("$.authenticationType").value("UsernamePasswordAuthenticationToken"));
    }

    @Test
    void apiIsStateless_noSessionIsCreated() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/employees").with(httpBasic("alice", "Alice@123")))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(result.getRequest().getSession(false)).isNull();
        assertThat(result.getResponse().getHeader("Set-Cookie")).isNull();
    }

    /**
     * CSRF is disabled for the API chain: a POST without a CSRF token is NOT
     * rejected with 403. It reaches Spring MVC, which answers 405 because the
     * endpoint only supports GET.
     */
    @Test
    void apiChain_doesNotRequireCsrfTokens() throws Exception {
        mockMvc.perform(post("/api/employees").with(httpBasic("admin", "Admin@123")))
                .andExpect(status().isMethodNotAllowed());
    }
}
