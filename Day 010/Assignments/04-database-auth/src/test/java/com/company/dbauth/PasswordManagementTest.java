package com.company.dbauth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.ResultActions;

import com.jayway.jsonpath.JsonPath;

/** Changing a password while logged in, and resetting a forgotten one with a one-time token. */
class PasswordManagementTest extends IntegrationTestSupport {

    @Autowired
    private JdbcTemplate jdbc;

    private ResultActions requestReset(String email) throws Exception {
        return mockMvc.perform(post("/api/auth/password-reset/request").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\"}"));
    }

    private String tokenFor(String email) throws Exception {
        return JsonPath.read(requestReset(email).andReturn().getResponse().getContentAsString(), "$.token");
    }

    private ResultActions confirm(String token, String newPassword) throws Exception {
        return mockMvc.perform(post("/api/auth/password-reset/confirm").contentType(MediaType.APPLICATION_JSON)
                .content("{\"token\":\"" + token + "\",\"newPassword\":\"" + newPassword + "\"}"));
    }

    @Test
    void changePassword_oldStopsWorking_newWorks() throws Exception {
        TestAccount account = registerFreshUser();

        mockMvc.perform(post("/api/me/password").with(httpBasic(account.username(), account.password()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currentPassword\":\"" + account.password() + "\",\"newPassword\":\"Changed@456\"}"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/me").with(httpBasic(account.username(), account.password())))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/me").with(httpBasic(account.username(), "Changed@456")))
                .andExpect(status().isOk());
    }

    @Test
    void changePassword_needsTheCorrectCurrentPassword_andADifferentNewOne() throws Exception {
        TestAccount account = registerFreshUser();
        mockMvc.perform(post("/api/me/password").with(httpBasic(account.username(), account.password()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currentPassword\":\"Guess@123\",\"newPassword\":\"Changed@456\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Current password is incorrect"));
        mockMvc.perform(post("/api/me/password").with(httpBasic(account.username(), account.password()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currentPassword\":\"" + account.password() + "\",\"newPassword\":\"" + account.password() + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("New password must be different from the current one"));
    }

    @Test
    void reset_fullFlow_tokenWorksOnce() throws Exception {
        TestAccount account = registerFreshUser();
        String token = tokenFor(account.email());

        confirm(token, "Reset@789").andExpect(status().isNoContent());

        mockMvc.perform(get("/api/me").with(httpBasic(account.username(), account.password())))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/me").with(httpBasic(account.username(), "Reset@789")))
                .andExpect(status().isOk());

        confirm(token, "Again@789")                                        // single use
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Reset token is invalid, expired or already used"));
    }

    /** Same answer for a known and an unknown e-mail: the endpoint does not reveal who has an account. */
    @Test
    void reset_unknownEmail_getsTheSameAnswer_withoutAToken() throws Exception {
        requestReset("nobody@company.com")
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.message").value("If an active account uses this e-mail address, a reset link has been sent"))
                .andExpect(jsonPath("$.token").doesNotExist());
        requestReset("vikram.nair@company.com")                            // disabled account: no token either
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.token").doesNotExist());
    }

    @Test
    void reset_expiredToken_isRefused() throws Exception {
        TestAccount account = registerFreshUser();
        String token = tokenFor(account.email());
        // pretend the 15 minutes have passed, for this token only
        jdbc.update("UPDATE password_reset_tokens SET expires_at = ? WHERE token_hash = SHA2(?, 256)",
                LocalDateTime.now().minusMinutes(1), token);

        confirm(token, "Reset@789").andExpect(status().isBadRequest());
    }

    @Test
    void reset_madeUpToken_isRefused() throws Exception {
        confirm("not-a-real-token", "Reset@789").andExpect(status().isBadRequest());
    }

    @Test
    void reset_alsoUnlocksALockedAccount() throws Exception {
        TestAccount account = registerFreshUser();
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get("/api/me").with(httpBasic(account.username(), "Wrong@123")));
        }
        confirm(tokenFor(account.email()), "Reset@789").andExpect(status().isNoContent());

        mockMvc.perform(get("/api/me").with(httpBasic(account.username(), "Reset@789")))
                .andExpect(status().isOk());
    }

    /** The table holds SHA-256 hashes (64 hex characters), never the token that was handed out. */
    @Test
    void onlyTheTokenHashIsStored() throws Exception {
        TestAccount account = registerFreshUser();
        String token = tokenFor(account.email());

        Integer rawMatches = jdbc.queryForObject(
                "SELECT COUNT(*) FROM password_reset_tokens WHERE token_hash = ?", Integer.class, token);
        Integer hashMatches = jdbc.queryForObject(
                "SELECT COUNT(*) FROM password_reset_tokens WHERE token_hash = SHA2(?, 256)", Integer.class, token);
        assertThat(rawMatches).isZero();
        assertThat(hashMatches).isEqualTo(1);
    }
}
