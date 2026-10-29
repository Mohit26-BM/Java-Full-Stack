package com.example.bank.auth;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import io.jsonwebtoken.Claims;

class AuthControllerTest {

    private static final String SECRET = "unit-test-secret-unit-test-secret-1234567890";
    private static final MediaType JSON = MediaType.APPLICATION_JSON;

    private final UserCredentialRepository users = mock(UserCredentialRepository.class);
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private final JwtService jwt = new JwtService(SECRET, 60_000);
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(users, encoder, jwt))
                .setControllerAdvice(new AuthExceptionHandler())
                .build();
    }

    private String bearer(String username, Role role) {
        return "Bearer " + jwt.generate(username, role, null);
    }

    // ---------------- register and sign in ----------------

    @Test
    void registerCreatesAPlainCustomerWithAHashedPassword() throws Exception {
        when(users.existsByUsername("newuser")).thenReturn(false);

        mockMvc.perform(post("/auth/register").contentType(JSON)
                .content("{\"username\":\"newuser\",\"password\":\"password123\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().string("User registered successfully"));

        ArgumentCaptor<UserCredential> saved = ArgumentCaptor.forClass(UserCredential.class);
        verify(users).save(saved.capture());
        assertEquals(Role.CUSTOMER, saved.getValue().getRole());
        assertEquals(null, saved.getValue().getCustomerId());
        assertEquals(true, encoder.matches("password123", saved.getValue().getPassword()));
    }

    @Test
    void registerCannotChooseARole() throws Exception {
        when(users.existsByUsername("sneaky")).thenReturn(false);

        mockMvc.perform(post("/auth/register").contentType(JSON)
                .content("{\"username\":\"sneaky\",\"password\":\"password123\",\"role\":\"ADMIN\"}"))
                .andExpect(status().isCreated());

        ArgumentCaptor<UserCredential> saved = ArgumentCaptor.forClass(UserCredential.class);
        verify(users).save(saved.capture());
        assertEquals(Role.CUSTOMER, saved.getValue().getRole());
    }

    @Test
    void registerRejectsADuplicateUsername() throws Exception {
        when(users.existsByUsername("taken")).thenReturn(true);

        mockMvc.perform(post("/auth/register").contentType(JSON)
                .content("{\"username\":\"taken\",\"password\":\"password123\"}"))
                .andExpect(status().isConflict())
                .andExpect(content().string("Username already exists"));

        verify(users, never()).save(any(UserCredential.class));
    }

    @Test
    void registerRejectsAShortPassword() throws Exception {
        mockMvc.perform(post("/auth/register").contentType(JSON)
                .content("{\"username\":\"newuser\",\"password\":\"short\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("password")));
    }

    @Test
    void tokenCarriesTheUsersRoleAndLinkedCustomer() throws Exception {
        when(users.findByUsername("aarav")).thenReturn(Optional.of(
                new UserCredential("aarav", encoder.encode("password123"), Role.CUSTOMER, 1L)));

        String token = mockMvc.perform(post("/auth/token").contentType(JSON)
                .content("{\"username\":\"aarav\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Claims claims = jwt.parse(token);
        assertEquals("aarav", claims.getSubject());
        assertEquals("CUSTOMER", claims.get("role", String.class));
        assertEquals(1L, claims.get("customerId", Number.class).longValue());
    }

    @Test
    void wrongPasswordAndUnknownUserGetTheSameAnswer() throws Exception {
        when(users.findByUsername("mohit")).thenReturn(Optional.of(new UserCredential("mohit", encoder.encode("password123"))));
        when(users.findByUsername("nobody")).thenReturn(Optional.empty());

        mockMvc.perform(post("/auth/token").contentType(JSON)
                .content("{\"username\":\"mohit\",\"password\":\"wrong-password\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid username or password"));

        mockMvc.perform(post("/auth/token").contentType(JSON)
                .content("{\"username\":\"nobody\",\"password\":\"password123\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid username or password"));
    }

    @Test
    void validateAcceptsAGoodTokenAndRejectsABadOne() throws Exception {
        String token = jwt.generate("mohit", Role.TELLER, null);

        mockMvc.perform(get("/auth/validate").param("token", token))
                .andExpect(status().isOk())
                .andExpect(content().string("Token is valid for mohit"));

        mockMvc.perform(get("/auth/validate").param("token", "not-a-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(is("Invalid or expired token")));
    }

    // ---------------- admin: roles and linking ----------------

    @Test
    void anAdminCanListUsersWithoutPasswords() throws Exception {
        when(users.findAll()).thenReturn(List.of(
                new UserCredential("aarav", "hash", Role.CUSTOMER, 1L), new UserCredential("teller", "hash", Role.TELLER, null)));

        mockMvc.perform(get("/auth/admin/users").header("Authorization", bearer("demo", Role.ADMIN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].username", is("aarav")))
                .andExpect(jsonPath("$[0].customerId", is(1)))
                .andExpect(jsonPath("$[0].password").doesNotExist());
    }

    @Test
    void anAdminCanSetARoleAndLinkACustomer() throws Exception {
        UserCredential user = new UserCredential("newuser", "hash");
        when(users.findByUsername("newuser")).thenReturn(Optional.of(user));
        when(users.save(any(UserCredential.class))).thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(put("/auth/admin/users/newuser").header("Authorization", bearer("demo", Role.ADMIN))
                .contentType(JSON).content("{\"role\":\"CUSTOMER\",\"customerId\":3}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role", is("CUSTOMER")))
                .andExpect(jsonPath("$.customerId", is(3)));

        assertEquals(3L, user.getCustomerId());
    }

    @Test
    void nonAdminsAreForbiddenFromTheAdminEndpoints() throws Exception {
        for (Role role : new Role[] {Role.CUSTOMER, Role.TELLER}) {
            mockMvc.perform(get("/auth/admin/users").header("Authorization", bearer("someone", role)))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string("Only an administrator can do this"));
            mockMvc.perform(put("/auth/admin/users/newuser").header("Authorization", bearer("someone", role))
                    .contentType(JSON).content("{\"role\":\"ADMIN\"}"))
                    .andExpect(status().isForbidden());
        }

        verify(users, never()).save(any(UserCredential.class));
    }

    @Test
    void theAdminEndpointsNeedAValidToken() throws Exception {
        mockMvc.perform(get("/auth/admin/users")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/auth/admin/users").header("Authorization", "Bearer garbage")).andExpect(status().isUnauthorized());
    }

    @Test
    void anAdminCannotRemoveTheirOwnAdminRole() throws Exception {
        when(users.findByUsername("demo")).thenReturn(Optional.of(new UserCredential("demo", "hash", Role.ADMIN, null)));

        mockMvc.perform(put("/auth/admin/users/demo").header("Authorization", bearer("demo", Role.ADMIN))
                .contentType(JSON).content("{\"role\":\"CUSTOMER\"}"))
                .andExpect(status().isConflict())
                .andExpect(content().string("You cannot remove your own admin role"));
    }

    @Test
    void settingARoleOnAnUnknownUserOrWithABadRoleFails() throws Exception {
        when(users.findByUsername("ghost")).thenReturn(Optional.empty());

        mockMvc.perform(put("/auth/admin/users/ghost").header("Authorization", bearer("demo", Role.ADMIN))
                .contentType(JSON).content("{\"role\":\"TELLER\"}"))
                .andExpect(status().isNotFound());

        mockMvc.perform(put("/auth/admin/users/ghost").header("Authorization", bearer("demo", Role.ADMIN))
                .contentType(JSON).content("{\"role\":\"BOSS\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("role must be CUSTOMER, TELLER or ADMIN")));
    }

}
