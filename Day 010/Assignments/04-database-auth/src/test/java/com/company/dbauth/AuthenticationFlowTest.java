package com.company.dbauth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.company.dbauth.security.DatabaseUserDetailsService;

/** Valid, invalid, disabled and unknown users, with the seeded demo accounts. */
class AuthenticationFlowTest extends IntegrationTestSupport {

    @Autowired
    private DatabaseUserDetailsService userDetailsService;

    @Test
    void validUser_isAuthenticated_withAuthoritiesFromTheDatabase() throws Exception {
        mockMvc.perform(get("/api/me").with(httpBasic("hana", "Hana@123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("hana"))
                .andExpect(jsonPath("$.fullName").value("Hana Kapoor"))
                .andExpect(jsonPath("$.authorities").value(org.hamcrest.Matchers.contains(
                        "EMPLOYEE_READ", "EMPLOYEE_WRITE", "ROLE_HR")));
    }

    /** manoj has two roles (MANAGER, USER): both grant EMPLOYEE_READ, which appears once. */
    @Test
    void twoRoles_giveTheUnionOfTheirPermissions() throws Exception {
        mockMvc.perform(get("/api/me").with(httpBasic("manoj", "Manoj@123")))
                .andExpect(jsonPath("$.authorities").value(org.hamcrest.Matchers.contains(
                        "EMPLOYEE_READ", "ROLE_MANAGER", "ROLE_USER")));
    }

    @Test
    void wrongPassword_andUnknownUser_getTheSame401() throws Exception {
        mockMvc.perform(get("/api/me").with(httpBasic("asha", "Wrong@123")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
        mockMvc.perform(get("/api/me").with(httpBasic("nobody", "Asha@123")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    /** vikram's row has enabled = FALSE: the CORRECT password is refused. */
    @Test
    void disabledUser_isRefused_evenWithTheRightPassword() throws Exception {
        mockMvc.perform(get("/api/me").with(httpBasic("vikram", "Vikram@123")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Account is disabled"));
    }

    @Test
    void noCredentials_gives401() throws Exception {
        mockMvc.perform(get("/api/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Authentication is required to access this resource"));
    }

    /** Usernames are stored in lower case and looked up in lower case. */
    @Test
    void usernameIsCaseInsensitive() throws Exception {
        mockMvc.perform(get("/api/me").with(httpBasic("ASHA", "Asha@123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("asha"));
    }

    @Test
    void permissionsFromTheDatabase_decideAccess() throws Exception {
        mockMvc.perform(get("/api/directory").with(httpBasic("asha", "Asha@123")))     // USER -> EMPLOYEE_READ
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].fullName").value(hasItem("Aditi Rao")))
                .andExpect(jsonPath("$[*].fullName").value(not(hasItem("Vikram Nair"))));  // disabled: hidden
        mockMvc.perform(get("/api/admin/users").with(httpBasic("hana", "Hana@123")))   // HR has no USER_MANAGE
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/admin/users").with(httpBasic("admin", "Admin@123")))
                .andExpect(status().isOk());
    }

    @Test
    void publicEndpoint_needsNoLogin() throws Exception {
        mockMvc.perform(get("/api/public/hello")).andExpect(status().isOk());
    }

    /** The UserDetailsService on its own: it loads, it does not check passwords. */
    @Test
    void userDetailsService_loadsHashAndAuthorities() {
        UserDetails hana = userDetailsService.loadUserByUsername("hana");

        assertThat(hana.getPassword()).startsWith("$2a$10$");
        assertThat(hana.getAuthorities()).extracting(GrantedAuthority::getAuthority)
                .containsExactly("EMPLOYEE_READ", "EMPLOYEE_WRITE", "ROLE_HR");
        assertThat(hana.isEnabled()).isTrue();
        assertThat(userDetailsService.loadUserByUsername("vikram").isEnabled()).isFalse();
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("nobody"))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void principalToString_neverShowsTheHash() {
        assertThat(userDetailsService.loadUserByUsername("hana").toString()).doesNotContain("$2a$");
    }
}
