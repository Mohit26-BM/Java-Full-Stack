package com.company.dbauth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.ResultActions;

import com.company.dbauth.entity.AppUser;

/** Registration: validation, BCrypt storage, default role, duplicates (in code and in MySQL). */
class RegistrationTest extends IntegrationTestSupport {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JdbcTemplate jdbc;

    private ResultActions register(String username, String email, String password) throws Exception {
        return mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content("""
                {"username":"%s","email":"%s","fullName":"Neha Joshi","password":"%s"}"""
                .formatted(username, email, password)));
    }

    @Test
    void validRegistration_createsAUserWithRoleUser_andOnlyAHashIsStored() throws Exception {
        register("Neha.Joshi", "Neha.Joshi@Company.com", "Neha@2026")
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", Matchers.startsWith("/api/admin/users/")))
                .andExpect(jsonPath("$.username").value("neha.joshi"))           // stored in lower case
                .andExpect(jsonPath("$.email").value("neha.joshi@company.com"))
                .andExpect(jsonPath("$.roles").value(Matchers.contains("USER")))
                .andExpect(jsonPath("$.enabled").value(true))
                .andExpect(content().string(Matchers.not(Matchers.containsString("$2a$"))))   // no hash in JSON
                .andExpect(content().string(Matchers.not(Matchers.containsString("Neha@2026"))));

        AppUser stored = users.findByUsername("neha.joshi").orElseThrow();
        assertThat(stored.getPasswordHash()).startsWith("$2a$10$").hasSize(60).isNotEqualTo("Neha@2026");
        assertThat(passwordEncoder.matches("Neha@2026", stored.getPasswordHash())).isTrue();

        // the new account can log in straight away
        mockMvc.perform(get("/api/me").with(httpBasic("neha.joshi", "Neha@2026")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authorities").value(Matchers.contains("EMPLOYEE_READ", "ROLE_USER")));
    }

    @Test
    void duplicateUsername_isRefused_evenInADifferentCase() throws Exception {
        register("ASHA", "someone.new@company.com", "Valid@123")
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Username 'asha' is already taken"));
    }

    @Test
    void duplicateEmail_isRefused() throws Exception {
        register("asha2", "ASHA.VERMA@company.com", "Valid@123")
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email 'asha.verma@company.com' is already registered"));
    }

    @Test
    void weakPassword_badEmail_andBadUsername_areRejected() throws Exception {
        register("a b", "not-an-email", "password")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "email must be a well-formed email address; "
                        + "password must be 8-72 characters with an upper-case letter, a lower-case letter, a digit and a symbol; "
                        + "username must be 3-30 characters: letters, digits, dot, dash or underscore"));
    }

    /** BCrypt only uses the first 72 bytes, so longer passwords are refused instead of silently cut. */
    @Test
    void passwordLongerThan72Characters_isRejected() throws Exception {
        register("longpass", "longpass@company.com", "Aa1!" + "x".repeat(69))
                .andExpect(status().isBadRequest());
    }

    /** The last line of defence: MySQL's UNIQUE constraint, even if the code check were skipped. */
    @Test
    void databaseUniqueConstraint_refusesADuplicateUsername() {
        assertThatThrownBy(() -> jdbc.update(
                "INSERT INTO users (username, email, full_name, password_hash, password_changed_at, created_at) "
                        + "VALUES ('asha', 'x@company.com', 'X', ?, NOW(), NOW())",
                passwordEncoder.encode("Valid@123")))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("uq_users_username");
    }

    /**
     * MySQL's CHECK constraint refuses a plain-text password, whatever code wrote it.
     * (MySQL reports CHECK violations as error 3819 with the generic SQL state HY000,
     * so Spring cannot classify it as DataIntegrityViolationException and wraps it in
     * UncategorizedSQLException; both are DataAccessExceptions.)
     */
    @Test
    void databaseCheckConstraint_refusesAPlainTextPassword() {
        assertThatThrownBy(() -> jdbc.update(
                "INSERT INTO users (username, email, full_name, password_hash, password_changed_at, created_at) "
                        + "VALUES ('plain', 'plain@company.com', 'P', 'Plain@123', NOW(), NOW())"))
                .isInstanceOf(DataAccessException.class)
                .hasMessageContaining("ck_users_password_is_bcrypt");
    }

    @Test
    void databaseCheckConstraint_refusesARoleNameWithThePrefix() {
        assertThatThrownBy(() -> jdbc.update("INSERT INTO roles (name, description) VALUES ('ROLE_X', 'x')"))
                .isInstanceOf(DataAccessException.class)
                .hasMessageContaining("ck_roles_name_no_prefix");
    }
}
