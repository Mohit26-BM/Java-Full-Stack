package com.company.rbac;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.jayway.jsonpath.JsonPath;

/** What allowed users actually get back, and the JSON error bodies. */
@SpringBootTest
@AutoConfigureMockMvc
class EmployeeApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void publicEndpoint_needsNoLogin() throws Exception {
        mockMvc.perform(get("/api/public/hello"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void noLogin_gives401Json_andWrongPassword_gives401WithItsOwnMessage() throws Exception {
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Authentication is required to access this resource"));
        mockMvc.perform(get("/api/employees").with(httpBasic("asha", "wrong")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    @Test
    void forbidden_gives403Json() throws Exception {
        mockMvc.perform(delete("/api/employees/4").with(httpBasic("hana", "Hana@123")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").value("You do not have permission to access this resource"))
                .andExpect(jsonPath("$.path").value("/api/employees/4"));
    }

    @Test
    void me_showsRolesWithThePrefix_andPermissionsWithout() throws Exception {
        mockMvc.perform(get("/api/me").with(httpBasic("hana", "Hana@123")))
                .andExpect(jsonPath("$.username").value("hana"))
                .andExpect(jsonPath("$.authorities[0]").value("EMPLOYEE_READ"))
                .andExpect(jsonPath("$.authorities[1]").value("EMPLOYEE_WRITE"))
                .andExpect(jsonPath("$.authorities[2]").value("ROLE_HR"));
    }

    @Test
    void team_isTheCallersDirectReports() throws Exception {
        mockMvc.perform(get("/api/employees/team").with(httpBasic("manoj", "Manoj@123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].name").value(hasItems("Asha Verma", "Ravi Iyer")))
                .andExpect(jsonPath("$.length()").value(2));
        mockMvc.perform(get("/api/employees/team").with(httpBasic("admin", "Admin@123")))
                .andExpect(jsonPath("$[*].name").value(hasItems("Hana Kapoor", "Manoj Pillai", "Karan Shah")));
    }

    @Test
    void hrCreates_transfers_andAdminDeletes() throws Exception {
        String created = mockMvc.perform(post("/api/employees").with(httpBasic("hana", "Hana@123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Neha Joshi","email":"neha.joshi@company.com",
                                 "department":"Engineering","designation":"QA Engineer","managerId":3}"""))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/employees/")))
                .andExpect(jsonPath("$.name").value("Neha Joshi"))
                .andReturn().getResponse().getContentAsString();
        int id = JsonPath.read(created, "$.id");

        mockMvc.perform(patch("/api/employees/{id}/department", id).with(httpBasic("hana", "Hana@123"))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"department\":\"Finance\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.department").value("Finance"));

        mockMvc.perform(delete("/api/employees/{id}", id).with(httpBasic("admin", "Admin@123")))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/employees/{id}", id).with(httpBasic("asha", "Asha@123")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Employee " + id + " not found"));
    }

    /** Validation runs only after authorization passed: HR gets 400, a USER gets 403 for the same body. */
    @Test
    void invalidBody_gives400ForHr_but403ForAUser() throws Exception {
        String invalid = "{\"name\":\"\",\"email\":\"not-an-email\",\"department\":\"IT\",\"designation\":\"Dev\"}";
        mockMvc.perform(post("/api/employees").with(httpBasic("hana", "Hana@123"))
                        .contentType(MediaType.APPLICATION_JSON).content(invalid))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("email must be a well-formed email address; name must not be blank"));
        mockMvc.perform(post("/api/employees").with(httpBasic("asha", "Asha@123"))
                        .contentType(MediaType.APPLICATION_JSON).content(invalid))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminUserList_neverContainsPasswordHashes() throws Exception {
        mockMvc.perform(get("/api/admin/users").with(httpBasic("admin", "Admin@123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(6))
                .andExpect(content().string(not(containsString("$2a$"))));
    }
}
