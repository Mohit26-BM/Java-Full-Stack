package com.company.rbac;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.BindException;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import com.company.rbac.config.PortalUsersProperties;
import com.company.rbac.security.PortalUserDetailsService;

/**
 * ROLE_ADMIN versus ADMIN, hasRole versus hasAuthority, and combined checks.
 * user(...).authorities(...) puts an exact authority list into the request, so
 * each test controls precisely which strings the user holds.
 */
@SpringBootTest
@AutoConfigureMockMvc
class RoleVersusAuthorityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PortalUserDetailsService userDetailsService;

    /** hasRole("ADMIN") looks for the authority "ROLE_ADMIN"; a bare "ADMIN" does not count. */
    @Test
    void hasRole_needsTheRolePrefix() throws Exception {
        mockMvc.perform(get("/api/admin/users").with(user("x").authorities(() -> "ADMIN")))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/admin/users").with(user("x").authorities(() -> "ROLE_ADMIN")))
                .andExpect(status().isOk());
        // roles("ADMIN") is only a shortcut for the authority "ROLE_ADMIN"
        mockMvc.perform(get("/api/admin/users").with(user("x").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    /** hasAuthority compares the exact string: "ROLE_" plays no part. */
    @Test
    void hasAuthority_comparesTheExactString() throws Exception {
        mockMvc.perform(get("/api/employees").with(user("x").authorities(() -> "EMPLOYEE_READ")))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/employees").with(user("x").authorities(() -> "ROLE_EMPLOYEE_READ")))
                .andExpect(status().isForbidden());
        // being an ADMIN does not imply a permission: authorities are not inherited
        mockMvc.perform(get("/api/employees").with(user("x").roles("ADMIN")))
                .andExpect(status().isForbidden());
    }

    /** Spring Security refuses hasRole("ROLE_ADMIN"): it would end up checking "ROLE_ROLE_ADMIN". */
    @Test
    void hasRole_rejectsAnAlreadyPrefixedName() {
        assertThatThrownBy(() -> AuthorityAuthorizationManager.hasRole("ROLE_ADMIN"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("should not start with ROLE_");
    }

    /** The department transfer needs (HR or ADMIN role) AND EMPLOYEE_WRITE: either half alone is refused. */
    @Test
    void combinedRule_needsTheRoleAndThePermission() throws Exception {
        String body = "{\"department\":\"Finance\"}";
        mockMvc.perform(patch("/api/employees/999/department").contentType(MediaType.APPLICATION_JSON).content(body)
                        .with(user("x").authorities(() -> "EMPLOYEE_WRITE")))                    // permission only
                .andExpect(status().isForbidden());
        mockMvc.perform(patch("/api/employees/999/department").contentType(MediaType.APPLICATION_JSON).content(body)
                        .with(user("x").authorities(() -> "ROLE_HR")))                           // role only
                .andExpect(status().isForbidden());
        mockMvc.perform(patch("/api/employees/999/department").contentType(MediaType.APPLICATION_JSON).content(body)
                        .with(user("x").authorities(() -> "ROLE_HR", () -> "EMPLOYEE_WRITE")))   // both
                .andExpect(status().isNotFound());                                               // allowed; id 999 does not exist
    }

    /** Deny by default: no rule allows PATCH /api/employees/{id}, so even ADMIN is refused. */
    @Test
    void anUnlistedOperation_isDeniedEvenForAdmin() throws Exception {
        mockMvc.perform(patch("/api/employees/4").with(user("admin")
                        .authorities(() -> "ROLE_ADMIN", () -> "EMPLOYEE_READ", () -> "EMPLOYEE_WRITE", () -> "EMPLOYEE_DELETE")))
                .andExpect(status().isForbidden());
    }

    @Test
    void realUsers_getRolesWithThePrefix_andPermissionsWithout() {
        assertThat(authoritiesOf("hana")).containsExactlyInAnyOrder("ROLE_HR", "EMPLOYEE_READ", "EMPLOYEE_WRITE");
        assertThat(authoritiesOf("rita")).containsExactlyInAnyOrder("ROLE_HR", "EMPLOYEE_READ");
        assertThat(authoritiesOf("hr-sync")).containsExactlyInAnyOrder("EMPLOYEE_READ", "EMPLOYEE_WRITE");
    }

    /** The User builder trap: authorities(...) REPLACES what roles(...) set (and the other way round). */
    @Test
    void userBuilder_rolesAndAuthoritiesOverwriteEachOther() {
        UserDetails user = User.withUsername("x").password("{noop}x")
                .roles("HR")
                .authorities("EMPLOYEE_READ")
                .build();

        assertThat(user.getAuthorities()).extracting(GrantedAuthority::getAuthority)
                .containsExactly("EMPLOYEE_READ");                                   // ROLE_HR is gone
    }

    /** "ROLE_ADMIN" in application.properties is not a Role: binding fails and the application does not start. */
    @Test
    void prefixedRoleInTheConfiguration_isRejected() {
        Binder binder = new Binder(new MapConfigurationPropertySource(Map.of(
                "portal.users[0].username", "x",
                "portal.users[0].password-hash", "$2a$10$kOgeb4kHaday1Qka07YUTOX7g41dLUctpUZvxlQEo5mN/fXnpz.E.",
                "portal.users[0].roles", "ROLE_ADMIN",
                "portal.users[0].permissions", "EMPLOYEE_READ")));

        assertThatThrownBy(() -> binder.bind("portal", PortalUsersProperties.class))
                .isInstanceOf(BindException.class);
    }

    private java.util.List<String> authoritiesOf(String username) {
        return userDetailsService.loadUserByUsername(username).getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
    }
}
