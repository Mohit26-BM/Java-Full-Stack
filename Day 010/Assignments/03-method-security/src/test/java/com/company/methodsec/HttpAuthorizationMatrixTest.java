package com.company.methodsec;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import com.company.methodsec.repository.EmployeeRepository;

/**
 * The README matrix over HTTP, with the real users. Most rows are decided by
 * method security alone (the URL chain only requires a login), including the
 * /api/tools/** rows, which have no URL rule of their own.
 *   Y = allowed (status given), - = 403. Data is reset before every case.
 */
@SpringBootTest
@AutoConfigureMockMvc
class HttpAuthorizationMatrixTest {

    private static final List<String> USERS = List.of("admin", "hana", "manoj", "asha", "ravi");

    private static final Map<String, String> PASSWORDS = Map.of(
            "admin", "Admin@123", "hana", "Hana@123", "manoj", "Manoj@123", "asha", "Asha@123", "ravi", "Ravi@123");

    private static final List<Rule> MATRIX = List.of(
            //        method   path                              body                               allowed admin hana manoj asha ravi
            new Rule("GET",    "/api/employees",                 null,                                200, "Y     Y    Y     Y    Y"),
            new Rule("GET",    "/api/employees/4",               null,                                200, "Y     Y    Y     Y    -"),
            new Rule("GET",    "/api/employees/4/salary",        null,                                200, "Y     Y    -     Y    -"),
            new Rule("PUT",    "/api/employees/4/salary",        "{\"salary\":99000}",                200, "Y     Y    -     -    -"),
            new Rule("PUT",    "/api/employees/2/salary",        "{\"salary\":999999}",               200, "Y     -    -     -    -"),
            new Rule("PATCH",  "/api/employees/4/designation",   "{\"designation\":\"Senior SE\"}",   200, "Y     Y    Y     -    -"),
            new Rule("GET",    "/api/managers/3/team",           null,                                200, "Y     Y    Y     -    -"),
            new Rule("GET",    "/api/managers/2/team",           null,                                200, "Y     Y    -     -    -"),
            new Rule("DELETE", "/api/employees/7",               null,                                204, "Y     -    -     -    -"),
            new Rule("GET",    "/api/tools/salary/5",            null,                                200, "Y     Y    -     -    Y"),
            new Rule("POST",   "/api/tools/offboard/7",          null,                                204, "Y     -    -     -    -"));

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository repository;

    @BeforeEach
    void resetData() {
        repository.resetToSampleData();
    }

    static Stream<Arguments> everyUserOnEveryOperation() {
        List<Arguments> cases = new ArrayList<>();
        for (Rule rule : MATRIX) {
            String[] cells = rule.access().trim().split("\\s+");
            for (int i = 0; i < USERS.size(); i++) {
                int expected = cells[i].equals("Y") ? rule.allowedStatus() : 403;
                cases.add(Arguments.of(USERS.get(i), rule.method(), rule.path(), rule.body(), expected));
            }
        }
        return cases.stream();
    }

    @ParameterizedTest(name = "{0} {1} {2} -> {4}")
    @MethodSource("everyUserOnEveryOperation")
    void accessMatchesTheMatrix(String user, String method, String path, String body, int expected) throws Exception {
        MockHttpServletRequestBuilder request = request(HttpMethod.valueOf(method), path)
                .with(httpBasic(user, PASSWORDS.get(user)));
        if (body != null) {
            request.contentType(MediaType.APPLICATION_JSON).content(body);
        }
        mockMvc.perform(request).andExpect(status().is(expected));
    }

    /** A denial thrown inside the service becomes the same 403 JSON as a URL denial. */
    @Test
    void methodSecurityDenial_givesThe403JsonBody() throws Exception {
        mockMvc.perform(request(HttpMethod.GET, "/api/tools/salary/5").with(httpBasic("asha", "Asha@123")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").value("You do not have permission to access this resource"))
                .andExpect(jsonPath("$.path").value("/api/tools/salary/5"));
    }

    @Test
    void noLogin_isStoppedByTheUrlChainWith401() throws Exception {
        mockMvc.perform(request(HttpMethod.GET, "/api/tools/salary/5"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(request(HttpMethod.GET, "/api/public/hello"))
                .andExpect(status().isOk());
    }

    @Test
    void responsesNeverContainSalary_exceptTheSalaryEndpoint() throws Exception {
        mockMvc.perform(request(HttpMethod.GET, "/api/employees/4").with(httpBasic("hana", "Hana@123")))
                .andExpect(jsonPath("$.salary").doesNotExist())
                .andExpect(jsonPath("$.designation").value("Software Engineer"));
        mockMvc.perform(request(HttpMethod.GET, "/api/employees/4/salary").with(httpBasic("hana", "Hana@123")))
                .andExpect(jsonPath("$.salary").value(95000));
    }

    /**
     * Unlike a URL rule, a method rule runs AFTER Spring MVC has validated the body (@Valid).
     * So an invalid body gets 400 even from a user the method would deny with 403.
     */
    @Test
    void invalidBody_gives400BeforeTheMethodRuleIsChecked() throws Exception {
        mockMvc.perform(request(HttpMethod.PUT, "/api/employees/4/salary").with(httpBasic("asha", "Asha@123"))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"salary\":-5}"))
                .andExpect(status().isBadRequest());
    }

    private record Rule(String method, String path, String body, int allowedStatus, String access) {
    }
}
