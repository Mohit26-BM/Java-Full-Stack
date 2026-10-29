package com.example.bank.account;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

class AccountControllerTest {

    private static final MediaType JSON = MediaType.APPLICATION_JSON;

    private final AccountService service = mock(AccountService.class);
    private final CustomerClient customers = mock(CustomerClient.class);
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AccountController(service, customers))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    /** Pretends the token filter has verified a token for this user and role. */
    private MockHttpServletRequestBuilder as(MockHttpServletRequestBuilder request, String username, String role, Long customerId) {
        request.requestAttr("username", username).requestAttr("role", role);
        return customerId == null ? request : request.requestAttr("customerId", customerId);
    }

    private MockHttpServletRequestBuilder teller(MockHttpServletRequestBuilder request) {
        return as(request, "mohit", "TELLER", null);
    }

    private MockHttpServletRequestBuilder admin(MockHttpServletRequestBuilder request) {
        return as(request, "admin", "ADMIN", null);
    }

    private static Caller caller(String username, String role) {
        return argThat(c -> c != null && username.equals(c.username()) && role.equals(c.role()));
    }

    private AccountResponse savings() {
        return new AccountResponse(5L, 1L, "SAVINGS", LocalDate.of(2024, 2, 1), new BigDecimal("5000.00"), "ACTIVE");
    }

    private TransactionResponse deposit() {
        return new TransactionResponse(1L, "ref-1", 5L, "DEPOSIT", new BigDecimal("250.00"), new BigDecimal("5250.00"),
                null, "salary", "mohit", LocalDateTime.of(2026, 10, 29, 12, 0));
    }

    private String openBody(long customerId, String type, String balance) {
        return "{\"customerId\":" + customerId + ",\"accountType\":\"" + type + "\",\"openingDate\":\""
                + LocalDate.now() + "\",\"balance\":" + balance + "}";
    }

    // ---------------- opening ----------------

    @Test
    void createChecksTheCustomerWithTheCallersTokenThenOpensTheAccount() throws Exception {
        when(service.open(any(AccountRequest.class), caller("mohit", "TELLER"))).thenReturn(savings());

        mockMvc.perform(teller(post("/api/v1/accounts")).header("Authorization", "Bearer abc")
                .contentType(JSON).content(openBody(1, "SAVINGS", "5000.00")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountno", is(5)))
                .andExpect(jsonPath("$.status", is("ACTIVE")));

        verify(customers).requireCustomer(1L, "Bearer abc");
    }

    @Test
    void aCustomerIsRefusedBeforeAnyRemoteCallIsMade() throws Exception {
        mockMvc.perform(as(post("/api/v1/accounts"), "aarav", "CUSTOMER", 1L).header("Authorization", "Bearer abc")
                .contentType(JSON).content(openBody(1, "SAVINGS", "5000")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", is("Only bank staff can open accounts")));

        verify(customers, never()).requireCustomer(any(), anyString());
        verify(service, never()).open(any(AccountRequest.class), any(Caller.class));
    }

    @Test
    void createFailsWith404WhenTheCustomerDoesNotExistAndOpensNothing() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer 99 does not exist"))
                .when(customers).requireCustomer(eq(99L), anyString());

        mockMvc.perform(teller(post("/api/v1/accounts")).header("Authorization", "Bearer abc")
                .contentType(JSON).content(openBody(99, "SAVINGS", "5000")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Customer 99 does not exist")));

        verify(service, never()).open(any(AccountRequest.class), any(Caller.class));
    }

    @Test
    void createFailsWith503WhenTheCustomerServiceIsDown() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Customer service is unavailable"))
                .when(customers).requireCustomer(eq(1L), anyString());

        mockMvc.perform(teller(post("/api/v1/accounts")).header("Authorization", "Bearer abc")
                .contentType(JSON).content(openBody(1, "SAVINGS", "5000")))
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    void createRejectsAnUnknownAccountTypeAndANegativeBalance() throws Exception {
        mockMvc.perform(teller(post("/api/v1/accounts")).header("Authorization", "Bearer abc")
                .contentType(JSON).content(openBody(1, "GOLD", "10")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Account type must be SAVINGS, CURRENT or FIXED_DEPOSIT")));

        mockMvc.perform(teller(post("/api/v1/accounts")).header("Authorization", "Bearer abc")
                .contentType(JSON).content(openBody(1, "SAVINGS", "-5")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Opening balance cannot be negative")));
    }

    // ---------------- reading ----------------

    @Test
    void getAndListPassTheCallerToTheService() throws Exception {
        when(service.get(eq(5L), caller("aarav", "CUSTOMER"))).thenReturn(savings());
        when(service.list(caller("mohit", "TELLER"))).thenReturn(List.of(savings()));
        when(service.get(eq(99L), any(Caller.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Account 99 does not exist"));

        mockMvc.perform(as(get("/api/v1/accounts/5"), "aarav", "CUSTOMER", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountType", is("SAVINGS")));

        mockMvc.perform(teller(get("/api/v1/accounts")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));

        mockMvc.perform(teller(get("/api/v1/accounts/99")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Account 99 does not exist")));
    }

    @Test
    void aRefusalFromTheServiceBecomes403() throws Exception {
        when(service.list(any(Caller.class))).thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied"));

        mockMvc.perform(get("/api/v1/accounts"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", is("Access denied")));
    }

    @Test
    void transactionsPassesLimitAndTheDateRange() throws Exception {
        when(service.statement(eq(5L), eq(LocalDate.of(2026, 10, 1)), eq(LocalDate.of(2026, 10, 31)), eq(5), any(Caller.class)))
                .thenReturn(List.of(deposit()));

        mockMvc.perform(teller(get("/api/v1/accounts/5/transactions"))
                .param("limit", "5").param("from", "2026-10-01").param("to", "2026-10-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type", is("DEPOSIT")))
                .andExpect(jsonPath("$[0].balanceAfter", is(5250.00)));
    }

    @Test
    void transactionsRejectsADateThatIsNotADate() throws Exception {
        mockMvc.perform(teller(get("/api/v1/accounts/5/transactions")).param("from", "yesterday"))
                .andExpect(status().isBadRequest());
    }

    // ---------------- money ----------------

    @Test
    void depositPassesTheKeyAndTheCallerToTheService() throws Exception {
        when(service.deposit(eq(5L), any(AmountRequest.class), eq("key-1"), caller("mohit", "TELLER"))).thenReturn(deposit());

        mockMvc.perform(teller(post("/api/v1/accounts/5/deposit")).header("Idempotency-Key", "key-1")
                .contentType(JSON).content("{\"amount\":250.00,\"description\":\"salary\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reference", is("ref-1")))
                .andExpect(jsonPath("$.performedBy", is("mohit")));
    }

    @Test
    void depositRejectsAZeroNegativeOrTooPreciseAmount() throws Exception {
        for (String bad : new String[] {"0", "-10", "10.123"}) {
            mockMvc.perform(teller(post("/api/v1/accounts/5/deposit")).contentType(JSON).content("{\"amount\":" + bad + "}"))
                    .andExpect(status().isBadRequest());
        }

        verify(service, never()).deposit(any(), any(), any(), any());
    }

    @Test
    void aMissingRoleIsPassedOnAndTheServiceRefusesIt() throws Exception {
        when(service.withdraw(eq(5L), any(AmountRequest.class), any(), any(Caller.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied"));

        mockMvc.perform(post("/api/v1/accounts/5/withdraw").contentType(JSON).content("{\"amount\":10}"))
                .andExpect(status().isForbidden());

        verify(service).withdraw(eq(5L), any(AmountRequest.class), any(), argThat(c -> c.role() == null && "unknown".equals(c.username())));
    }

    @Test
    void withdrawReturnsTheServicesRuleViolationAs409() throws Exception {
        when(service.withdraw(eq(5L), any(AmountRequest.class), any(), any(Caller.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Insufficient funds"));

        mockMvc.perform(teller(post("/api/v1/accounts/5/withdraw")).contentType(JSON).content("{\"amount\":999999}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is("Insufficient funds")));
    }

    @Test
    void transferCallsTheServiceWithTheCaller() throws Exception {
        when(service.transfer(any(TransferRequest.class), any(), any(Caller.class))).thenReturn(deposit());

        mockMvc.perform(as(post("/api/v1/accounts/transfer"), "aarav", "CUSTOMER", 1L).contentType(JSON)
                .content("{\"fromAccount\":5,\"toAccount\":6,\"amount\":100.00,\"description\":\"rent\"}"))
                .andExpect(status().isOk());

        verify(service).transfer(any(TransferRequest.class), any(), caller("aarav", "CUSTOMER"));
    }

    @Test
    void transferNeedsBothAccountsAndAnAmount() throws Exception {
        mockMvc.perform(teller(post("/api/v1/accounts/transfer")).contentType(JSON).content("{\"fromAccount\":5}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void aLostRaceBetweenTwoRequestsGives409() throws Exception {
        when(service.withdraw(eq(5L), any(AmountRequest.class), any(), any(Caller.class)))
                .thenThrow(new ObjectOptimisticLockingFailureException(Account.class, 5L));

        mockMvc.perform(teller(post("/api/v1/accounts/5/withdraw")).contentType(JSON).content("{\"amount\":10}"))
                .andExpect(status().isConflict());
    }

    // ---------------- administration ----------------

    @Test
    void statusCanBeChangedToAKnownValueOnly() throws Exception {
        when(service.changeStatus(eq(5L), eq(AccountStatus.FROZEN), caller("admin", "ADMIN"))).thenReturn(
                new AccountResponse(5L, 1L, "SAVINGS", LocalDate.of(2024, 2, 1), new BigDecimal("5000.00"), "FROZEN"));

        mockMvc.perform(admin(patch("/api/v1/accounts/5/status")).contentType(JSON).content("{\"status\":\"FROZEN\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("FROZEN")));

        mockMvc.perform(admin(patch("/api/v1/accounts/5/status")).contentType(JSON).content("{\"status\":\"DELETED\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Status must be ACTIVE, FROZEN or CLOSED")));
    }

    @Test
    void interestRunTakesAMonthAndDefaultsToThisOne() throws Exception {
        when(service.accrueInterest(eq(YearMonth.of(2020, 3)), any(Caller.class)))
                .thenReturn(new InterestRunResponse("2020-03", 2, new BigDecimal("3850.00")));
        when(service.accrueInterest(eq(YearMonth.now()), any(Caller.class)))
                .thenReturn(new InterestRunResponse(YearMonth.now().toString(), 0, BigDecimal.ZERO));

        mockMvc.perform(admin(post("/api/v1/accounts/interest")).param("month", "2020-03"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountsCredited", is(2)))
                .andExpect(jsonPath("$.totalInterest", is(3850.00)));

        mockMvc.perform(admin(post("/api/v1/accounts/interest")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.month", is(YearMonth.now().toString())));
    }

    @Test
    void interestRunRejectsABadMonth() throws Exception {
        mockMvc.perform(admin(post("/api/v1/accounts/interest")).param("month", "October"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("month must look like 2026-10")));

        verify(service, never()).accrueInterest(any(), any());
    }

    @Test
    void auditTrailIsReturnedFromTheService() throws Exception {
        when(service.auditTrail(eq(10), caller("admin", "ADMIN"))).thenReturn(List.of(
                new AuditResponse(1L, "admin", "ADMIN", "STATUS_CHANGED", 5L, "ACTIVE -> FROZEN", LocalDateTime.of(2026, 10, 29, 12, 0))));

        mockMvc.perform(admin(get("/api/v1/accounts/audit")).param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].action", is("STATUS_CHANGED")))
                .andExpect(jsonPath("$[0].actor", is("admin")));
    }

}
