package com.example.bank.customer;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class CustomerControllerTest {

    private final CustomerRepository customers = mock(CustomerRepository.class);
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new CustomerController(customers))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    /** Pretends the token filter has verified a token for this role (and linked customer, if any). */
    private MockHttpServletRequestBuilder as(MockHttpServletRequestBuilder request, String role, Long customerId) {
        request.requestAttr("username", "tester").requestAttr("role", role);
        return customerId == null ? request : request.requestAttr("customerId", customerId);
    }

    private MockHttpServletRequestBuilder staff(MockHttpServletRequestBuilder request) {
        return as(request, "TELLER", null);
    }

    private String body(String email, LocalDate dateOfBirth) {
        return "{\"name\":\"Asha Rao\",\"email\":\"" + email + "\",\"dateOfBirth\":\"" + dateOfBirth
                + "\",\"gender\":\"Female\",\"city\":\"Pune\"}";
    }

    private Customer asha() {
        return new Customer("Asha Rao", "asha@example.com", LocalDate.of(1995, 3, 14), "Female", "Pune");
    }

    // ---------------- what staff can do ----------------

    @Test
    void createReturns201WithTheSavedCustomer() throws Exception {
        when(customers.existsByEmailIgnoreCase("asha@example.com")).thenReturn(false);
        when(customers.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(staff(post("/api/v1/customers")).contentType(MediaType.APPLICATION_JSON)
                .content(body("asha@example.com", LocalDate.of(1995, 3, 14))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Asha Rao")))
                .andExpect(jsonPath("$.city", is("Pune")));
    }

    @Test
    void createRejectsACustomerUnder18() throws Exception {
        mockMvc.perform(staff(post("/api/v1/customers")).contentType(MediaType.APPLICATION_JSON)
                .content(body("kid@example.com", LocalDate.now().minusYears(10))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Customer must be at least 18 years old")));

        verify(customers, never()).save(any(Customer.class));
    }

    @Test
    void createRejectsADuplicateEmail() throws Exception {
        when(customers.existsByEmailIgnoreCase("asha@example.com")).thenReturn(true);

        mockMvc.perform(staff(post("/api/v1/customers")).contentType(MediaType.APPLICATION_JSON)
                .content(body("asha@example.com", LocalDate.of(1995, 3, 14))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)));
    }

    @Test
    void createRejectsAnInvalidEmail() throws Exception {
        mockMvc.perform(staff(post("/api/v1/customers")).contentType(MediaType.APPLICATION_JSON)
                .content(body("not-an-email", LocalDate.of(1995, 3, 14))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Email is not valid")));
    }

    @Test
    void staffCanReadAnyCustomerAndGet404ForAMissingOne() throws Exception {
        when(customers.findById(1L)).thenReturn(Optional.of(asha()));
        when(customers.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(as(get("/api/v1/customers/1"), "ADMIN", null))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("asha@example.com")));

        mockMvc.perform(staff(get("/api/v1/customers/99")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Customer 99 does not exist")));
    }

    @Test
    void staffCanListAllCustomers() throws Exception {
        when(customers.findAll()).thenReturn(List.of(asha(),
                new Customer("Ravi Kumar", "ravi@example.com", LocalDate.of(1990, 1, 2), "Male", "Delhi")));

        mockMvc.perform(staff(get("/api/v1/customers")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[1].name", is("Ravi Kumar")));
    }

    // ---------------- what a customer login can do ----------------

    @Test
    void aCustomerCanReadOnlyTheirOwnRecord() throws Exception {
        when(customers.findById(1L)).thenReturn(Optional.of(asha()));

        mockMvc.perform(as(get("/api/v1/customers/1"), "CUSTOMER", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Asha Rao")));

        mockMvc.perform(as(get("/api/v1/customers/2"), "CUSTOMER", 1L))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", is("You can only view your own customer record")));
    }

    @Test
    void aCustomerCannotListOrRegisterCustomers() throws Exception {
        mockMvc.perform(as(get("/api/v1/customers"), "CUSTOMER", 1L))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", is("Only bank staff can list all customers")));

        mockMvc.perform(as(post("/api/v1/customers"), "CUSTOMER", 1L).contentType(MediaType.APPLICATION_JSON)
                .content(body("asha@example.com", LocalDate.of(1995, 3, 14))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", is("Only bank staff can register customers")));

        verify(customers, never()).save(any(Customer.class));
        verify(customers, never()).findAll();
    }

    @Test
    void aCustomerLoginThatIsNotLinkedToARecordSeesNothing() throws Exception {
        mockMvc.perform(as(get("/api/v1/customers/1"), "CUSTOMER", null)).andExpect(status().isForbidden());
    }

    @Test
    void aTokenWithoutARoleIsRefusedEverywhere() throws Exception {
        mockMvc.perform(get("/api/v1/customers")).andExpect(status().isForbidden());
        mockMvc.perform(get("/api/v1/customers/1")).andExpect(status().isForbidden());
        mockMvc.perform(post("/api/v1/customers").contentType(MediaType.APPLICATION_JSON)
                .content(body("asha@example.com", LocalDate.of(1995, 3, 14)))).andExpect(status().isForbidden());
    }

    @Test
    void callerIsReadFromTheRequestAttributes() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("username", "aarav");
        request.setAttribute("role", "CUSTOMER");
        request.setAttribute("customerId", 7);

        Caller caller = Caller.from(request);

        org.junit.jupiter.api.Assertions.assertEquals("aarav", caller.username());
        org.junit.jupiter.api.Assertions.assertTrue(caller.owns(7L));
        org.junit.jupiter.api.Assertions.assertFalse(caller.owns(8L));
        org.junit.jupiter.api.Assertions.assertFalse(caller.isStaff());
    }

}
