package com.company.rbac;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/**
 * The access-control matrix from the README, tested for EVERY role on EVERY
 * endpoint with the real users and passwords (HTTP Basic).
 *
 *   Y = allowed: the request passes security and reaches the controller, which
 *       answers with its normal status (200 / 201, or 404 for the unknown id 999)
 *   - = denied:  403 from the AccessDeniedHandler, the controller never runs
 *   no login:    401 from the AuthenticationEntryPoint
 */
@SpringBootTest
@AutoConfigureMockMvc
class AccessControlMatrixTest {

    private static final List<String> USERS = List.of("admin", "hana", "rita", "manoj", "asha", "hr-sync");

    private static final Map<String, String> PASSWORDS = Map.of(
            "admin", "Admin@123", "hana", "Hana@123", "rita", "Rita@123",
            "manoj", "Manoj@123", "asha", "Asha@123", "hr-sync", "Sync@123");

    private static final String EMPLOYEE_JSON = """
            {"name":"Test Person","email":"test.person@company.com",
             "department":"Finance","designation":"Analyst","managerId":1}""";
    private static final String TRANSFER_JSON = """
            {"department":"Finance"}""";

    private static final List<Rule> MATRIX = List.of(
            //        method   path                                    allowed  admin hana rita manoj asha hr-sync
            new Rule("GET",    "/api/employees",                         200,   "Y     Y    Y    Y     Y    Y"),
            new Rule("GET",    "/api/employees/4",                       200,   "Y     Y    Y    Y     Y    Y"),
            new Rule("GET",    "/api/employees/team",                    200,   "Y     -    -    Y     -    -"),
            new Rule("POST",   "/api/employees",                         201,   "Y     Y    -    -     -    Y"),
            new Rule("PUT",    "/api/employees/999",                     404,   "Y     Y    -    -     -    Y"),
            new Rule("PATCH",  "/api/employees/999/department",          404,   "Y     Y    -    -     -    -"),
            new Rule("DELETE", "/api/employees/999",                     404,   "Y     -    -    -     -    -"),
            new Rule("GET",    "/api/hr/reports/headcount",              200,   "Y     Y    Y    -     -    -"),
            new Rule("GET",    "/api/admin/users",                       200,   "Y     -    -    -     -    -"),
            new Rule("GET",    "/api/me",                                200,   "Y     Y    Y    Y     Y    Y"));

    @Autowired
    private MockMvc mockMvc;

    static Stream<Arguments> everyRoleOnEveryEndpoint() {
        List<Arguments> cases = new ArrayList<>();
        for (Rule rule : MATRIX) {
            String[] cells = rule.access().trim().split("\\s+");
            for (int i = 0; i < USERS.size(); i++) {
                int expected = cells[i].equals("Y") ? rule.allowedStatus() : 403;
                cases.add(Arguments.of(USERS.get(i), rule.method(), rule.path(), expected));
            }
        }
        return cases.stream();
    }

    static Stream<Arguments> everyEndpointWithoutLogin() {
        return MATRIX.stream().map(rule -> Arguments.of(rule.method(), rule.path()));
    }

    @ParameterizedTest(name = "{0} {1} {2} -> {3}")
    @MethodSource("everyRoleOnEveryEndpoint")
    void accessMatchesTheMatrix(String user, String method, String path, int expectedStatus) throws Exception {
        mockMvc.perform(requestFor(method, path).with(httpBasic(user, PASSWORDS.get(user))))
                .andExpect(status().is(expectedStatus));
    }

    @ParameterizedTest(name = "no login {0} {1} -> 401")
    @MethodSource("everyEndpointWithoutLogin")
    void everyEndpointNeedsALogin(String method, String path) throws Exception {
        mockMvc.perform(requestFor(method, path))
                .andExpect(status().isUnauthorized());
    }

    private static MockHttpServletRequestBuilder requestFor(String method, String path) {
        MockHttpServletRequestBuilder request = request(HttpMethod.valueOf(method), path);
        return switch (method) {
            case "POST", "PUT" -> request.contentType(MediaType.APPLICATION_JSON).content(EMPLOYEE_JSON);
            case "PATCH" -> request.contentType(MediaType.APPLICATION_JSON).content(TRANSFER_JSON);
            default -> request;
        };
    }

    private record Rule(String method, String path, int allowedStatus, String access) {
    }
}
