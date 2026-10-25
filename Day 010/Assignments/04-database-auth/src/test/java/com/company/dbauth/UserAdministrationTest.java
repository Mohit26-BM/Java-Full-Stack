package com.company.dbauth;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

/**
 * Admin changes are read from the database on the NEXT request: HTTP Basic
 * calls the UserDetailsService every time, so there is no stale login to wait out.
 */
class UserAdministrationTest extends IntegrationTestSupport {

    @Test
    void disablingAUser_takesEffectOnTheirNextRequest() throws Exception {
        TestAccount account = registerFreshUser();
        mockMvc.perform(get("/api/me").with(httpBasic(account.username(), account.password())))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/api/admin/users/{id}/enabled", account.id()).with(httpBasic("admin", "Admin@123"))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"enabled\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled").value(false));

        mockMvc.perform(get("/api/me").with(httpBasic(account.username(), account.password())))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Account is disabled"));

        mockMvc.perform(patch("/api/admin/users/{id}/enabled", account.id()).with(httpBasic("admin", "Admin@123"))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"enabled\":true}"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/me").with(httpBasic(account.username(), account.password())))
                .andExpect(status().isOk());
    }

    @Test
    void anAdminCannotDisableTheirOwnAccount() throws Exception {
        Long adminId = users.findByUsername("admin").orElseThrow().getId();
        mockMvc.perform(patch("/api/admin/users/{id}/enabled", adminId).with(httpBasic("admin", "Admin@123"))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"enabled\":false}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("You cannot disable your own account"));
    }

    @Test
    void changingRoles_changesThePermissionsOnTheNextRequest() throws Exception {
        TestAccount account = registerFreshUser();

        mockMvc.perform(put("/api/admin/users/{id}/roles", account.id()).with(httpBasic("admin", "Admin@123"))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"roles\":[\"hr\",\"USER\"]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles").value(Matchers.contains("HR", "USER")));

        mockMvc.perform(get("/api/me").with(httpBasic(account.username(), account.password())))
                .andExpect(jsonPath("$.authorities").value(Matchers.contains(
                        "EMPLOYEE_READ", "EMPLOYEE_WRITE", "ROLE_HR", "ROLE_USER")));
    }

    @Test
    void unknownRole_isRejected_andNothingChanges() throws Exception {
        TestAccount account = registerFreshUser();
        mockMvc.perform(put("/api/admin/users/{id}/roles", account.id()).with(httpBasic("admin", "Admin@123"))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"roles\":[\"USER\",\"SUPERUSER\"]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Unknown role(s): SUPERUSER"));
        mockMvc.perform(get("/api/me").with(httpBasic(account.username(), account.password())))
                .andExpect(jsonPath("$.authorities").value(Matchers.contains("EMPLOYEE_READ", "ROLE_USER")));
    }

    @Test
    void unknownUser_gives404() throws Exception {
        mockMvc.perform(put("/api/admin/users/999999/roles").with(httpBasic("admin", "Admin@123"))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"roles\":[\"USER\"]}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void userList_showsStatus_butNeverAHash() throws Exception {
        mockMvc.perform(get("/api/admin/users").with(httpBasic("admin", "Admin@123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.username == 'vikram')].enabled").value(Matchers.contains(false)))
                .andExpect(jsonPath("$[?(@.username == 'manoj')].roles[*]").value(Matchers.contains("MANAGER", "USER")))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content()
                        .string(Matchers.not(Matchers.containsString("$2a$"))));
    }

    @Test
    void nonAdmins_cannotManageUsers() throws Exception {
        mockMvc.perform(put("/api/admin/users/1/roles").with(httpBasic("hana", "Hana@123"))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"roles\":[\"ADMIN\"]}"))
                .andExpect(status().isForbidden());
    }
}
