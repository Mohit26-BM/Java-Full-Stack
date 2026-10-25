package com.company.dbauth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.company.dbauth.entity.AppUser;

/** 5 wrong passwords lock the account for 15 minutes; success, expiry or an admin end it. */
class AccountLockingTest extends IntegrationTestSupport {

    private void failLogins(TestAccount account, int times) throws Exception {
        for (int i = 0; i < times; i++) {
            mockMvc.perform(get("/api/me").with(httpBasic(account.username(), "Wrong@123")))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value("Invalid username or password"));
        }
    }

    private AppUser reload(TestAccount account) {
        return users.findById(account.id()).orElseThrow();
    }

    @Test
    void fiveWrongPasswords_lockTheAccount_evenAgainstTheRightPassword() throws Exception {
        TestAccount account = registerFreshUser();

        failLogins(account, 5);

        AppUser locked = reload(account);
        assertThat(locked.getFailedLoginAttempts()).isEqualTo(5);
        assertThat(locked.getLockedUntil()).isAfter(LocalDateTime.now().plusMinutes(14));

        mockMvc.perform(get("/api/me").with(httpBasic(account.username(), account.password())))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Account is temporarily locked after too many failed logins"));
    }

    @Test
    void aSuccessfulLogin_resetsTheCounter() throws Exception {
        TestAccount account = registerFreshUser();
        failLogins(account, 3);
        assertThat(reload(account).getFailedLoginAttempts()).isEqualTo(3);

        mockMvc.perform(get("/api/me").with(httpBasic(account.username(), account.password())))
                .andExpect(status().isOk());

        assertThat(reload(account).getFailedLoginAttempts()).isZero();
    }

    @Test
    void theLockEndsByItself_whenLockedUntilHasPassed() throws Exception {
        TestAccount account = registerFreshUser();
        failLogins(account, 5);

        AppUser user = reload(account);
        user.setLockedUntil(LocalDateTime.now().minusSeconds(1));        // pretend 15 minutes have passed
        users.save(user);

        mockMvc.perform(get("/api/me").with(httpBasic(account.username(), account.password())))
                .andExpect(status().isOk());
        assertThat(reload(account).getFailedLoginAttempts()).isZero();
    }

    @Test
    void anAdminCanUnlock() throws Exception {
        TestAccount account = registerFreshUser();
        failLogins(account, 5);

        mockMvc.perform(post("/api/admin/users/{id}/unlock", account.id()).with(httpBasic("admin", "Admin@123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.locked").value(false))
                .andExpect(jsonPath("$.failedLoginAttempts").value(0));

        mockMvc.perform(get("/api/me").with(httpBasic(account.username(), account.password())))
                .andExpect(status().isOk());
    }

    /** Spring checks the account flags BEFORE the password: a locked account says so to anyone. */
    @Test
    void lockedMessage_isShownEvenForAWrongPassword() throws Exception {
        TestAccount account = registerFreshUser();
        failLogins(account, 5);

        mockMvc.perform(get("/api/me").with(httpBasic(account.username(), "Wrong@123")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Account is temporarily locked after too many failed logins"));
        assertThat(reload(account).getFailedLoginAttempts()).isEqualTo(5);  // not counted any further
    }
}
