package com.example.bank.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

/** Who may do what, the self-service limit, interest, the audit trail and date-range statements. */
@ExtendWith(MockitoExtension.class)
class AccountAccessTest {

    private static final Caller TELLER = new Caller("teller", "TELLER", null);
    private static final Caller ADMIN = new Caller("admin", "ADMIN", null);
    private static final Caller AARAV = new Caller("aarav", "CUSTOMER", 1L);        // owns customer 1's accounts
    private static final Caller UNLINKED = new Caller("newuser", "CUSTOMER", null);
    private static final Caller NO_ROLE = new Caller("old-token", null, null);

    @Mock
    private AccountRepository accounts;
    @Mock
    private AccountTransactionRepository ledger;
    @Mock
    private AuditEntryRepository audit;

    private AccountService service;

    @BeforeEach
    void setUp() {
        service = new AccountService(accounts, ledger, audit);
        lenient().when(ledger.sumSince(anyLong(), any(), any())).thenReturn(BigDecimal.ZERO);
        lenient().when(ledger.save(any(AccountTransaction.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private Account account(long id, long customerId, String type, String balance) {
        Account a = new Account(customerId, type, LocalDate.of(2024, 1, 1), new BigDecimal(balance));
        ReflectionTestUtils.setField(a, "accountno", id);
        lenient().when(accounts.findForUpdate(id)).thenReturn(Optional.of(a));
        lenient().when(accounts.findById(id)).thenReturn(Optional.of(a));
        return a;
    }

    private AmountRequest amount(String value) {
        return new AmountRequest(new BigDecimal(value), null);
    }

    private ResponseStatusException rejected(Runnable action, HttpStatus status) {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, action::run);
        assertEquals(status, ex.getStatusCode());
        return ex;
    }

    // ---------------- reading ----------------

    @Test
    void staffSeeAllAccountsAndACustomerSeesOnlyTheirOwn() {
        Account mine = account(1, 1, "SAVINGS", "5000.00");
        Account theirs = account(2, 2, "SAVINGS", "5000.00");
        when(accounts.findAll()).thenReturn(List.of(mine, theirs));
        when(accounts.findByCustomerId(1L)).thenReturn(List.of(mine));

        assertEquals(2, service.list(TELLER).size());
        assertEquals(2, service.list(ADMIN).size());
        List<AccountResponse> own = service.list(AARAV);
        assertEquals(1, own.size());
        assertEquals(1L, own.get(0).accountno());
    }

    @Test
    void aCustomerLoginNotLinkedToARecordSeesNoAccountsAndATokenWithoutARoleIsRefused() {
        assertEquals(0, service.list(UNLINKED).size());
        rejected(() -> service.list(NO_ROLE), HttpStatus.FORBIDDEN);
    }

    @Test
    void aCustomerGets404ForSomeoneElsesAccountSoNumbersCannotBeProbed() {
        account(2, 2, "SAVINGS", "5000.00");

        ResponseStatusException ex = rejected(() -> service.get(2L, AARAV), HttpStatus.NOT_FOUND);

        assertEquals("Account 2 does not exist", ex.getReason());
        rejected(() -> service.statement(2L, null, null, 20, AARAV), HttpStatus.NOT_FOUND);
    }

    @Test
    void aCustomerCanReadTheirOwnAccountAndStatement() {
        account(1, 1, "SAVINGS", "5000.00");
        when(ledger.findStatement(anyLong(), any(), any(), any(Pageable.class))).thenReturn(List.of());

        assertEquals(1L, service.get(1L, AARAV).accountno());
        assertEquals(0, service.statement(1L, null, null, 20, AARAV).size());
    }

    @Test
    void aTokenWithoutARoleCannotReadAnAccount() {
        account(1, 1, "SAVINGS", "5000.00");

        rejected(() -> service.get(1L, NO_ROLE), HttpStatus.FORBIDDEN);
    }

    // ---------------- what a customer may and may not do ----------------

    @Test
    void customersCannotOpenAccountsOrDeposit() {
        account(1, 1, "SAVINGS", "5000.00");

        rejected(() -> service.open(new AccountRequest(1L, "SAVINGS", LocalDate.now(), new BigDecimal("5000")), AARAV), HttpStatus.FORBIDDEN);
        rejected(() -> service.deposit(1L, amount("100"), null, AARAV), HttpStatus.FORBIDDEN);

        verify(accounts, never()).save(any(Account.class));
        verify(ledger, never()).save(any(AccountTransaction.class));
    }

    @Test
    void aCustomerCanWithdrawFromTheirOwnAccountButNotFromSomeoneElses() {
        Account mine = account(1, 1, "SAVINGS", "5000.00");
        Account theirs = account(2, 2, "SAVINGS", "5000.00");

        service.withdraw(1L, amount("500"), null, AARAV);
        assertEquals(new BigDecimal("4500.00"), mine.getBalance());

        rejected(() -> service.withdraw(2L, amount("500"), null, AARAV), HttpStatus.NOT_FOUND);
        assertEquals(new BigDecimal("5000.00"), theirs.getBalance());
    }

    @Test
    void aCustomerCanTransferFromTheirOwnAccountToAnyoneButNotFromSomeoneElses() {
        Account mine = account(1, 1, "SAVINGS", "5000.00");
        Account theirs = account(2, 2, "CURRENT", "100.00");

        service.transfer(new TransferRequest(1L, 2L, new BigDecimal("1000"), "gift"), null, AARAV);
        assertEquals(new BigDecimal("4000.00"), mine.getBalance());
        assertEquals(new BigDecimal("1100.00"), theirs.getBalance());

        rejected(() -> service.transfer(new TransferRequest(2L, 1L, new BigDecimal("50"), null), null, AARAV), HttpStatus.NOT_FOUND);
        assertEquals(new BigDecimal("1100.00"), theirs.getBalance());
    }

    @Test
    void aReplayedRequestStillNeedsAccessToTheAccount() {
        account(2, 2, "SAVINGS", "5000.00");
        AccountTransaction first = new AccountTransaction(2L, TransactionType.WITHDRAWAL, new BigDecimal("10.00"),
                new BigDecimal("4990.00"), null, "ref", null, "someone", "key-9");
        when(ledger.findByIdempotencyKey("key-9")).thenReturn(Optional.of(first));

        rejected(() -> service.withdraw(2L, amount("10"), "key-9", AARAV), HttpStatus.NOT_FOUND);
    }

    @Test
    void selfServiceHasAPerTransactionLimitThatStaffDoNotHave() {
        account(1, 1, "CURRENT", "500000.00");

        ResponseStatusException ex = rejected(() -> service.withdraw(1L, amount("100000.01"), null, AARAV), HttpStatus.CONFLICT);
        assertTrue(ex.getReason().startsWith("Amounts above 100000.00 cannot be done in self-service"));
        rejected(() -> service.transfer(new TransferRequest(1L, 2L, new BigDecimal("100000.01"), null), null, AARAV), HttpStatus.NOT_FOUND);

        service.withdraw(1L, amount("100000.00"), null, AARAV);
        service.withdraw(1L, amount("150000"), null, TELLER);
    }

    @Test
    void aTellerCanDoEverythingExceptAdministration() {
        account(1, 1, "SAVINGS", "5000.00");

        service.deposit(1L, amount("100"), null, TELLER);
        service.withdraw(1L, amount("100"), null, TELLER);
        rejected(() -> service.changeStatus(1L, AccountStatus.FROZEN, TELLER), HttpStatus.FORBIDDEN);
        rejected(() -> service.accrueInterest(YearMonth.of(2026, 10), TELLER), HttpStatus.FORBIDDEN);
        rejected(() -> service.auditTrail(10, TELLER), HttpStatus.FORBIDDEN);
    }

    @Test
    void customersCannotAdministerAnything() {
        account(1, 1, "SAVINGS", "5000.00");

        rejected(() -> service.changeStatus(1L, AccountStatus.FROZEN, AARAV), HttpStatus.FORBIDDEN);
        rejected(() -> service.accrueInterest(YearMonth.of(2026, 10), AARAV), HttpStatus.FORBIDDEN);
        rejected(() -> service.auditTrail(10, AARAV), HttpStatus.FORBIDDEN);
    }

    // ---------------- audit ----------------

    @Test
    void statusChangesAreWrittenToTheAuditTrail() {
        account(1, 1, "SAVINGS", "5000.00");

        service.changeStatus(1L, AccountStatus.FROZEN, ADMIN);

        ArgumentCaptor<AuditEntry> entry = ArgumentCaptor.forClass(AuditEntry.class);
        verify(audit).save(entry.capture());
        assertEquals("admin", entry.getValue().getActor());
        assertEquals("ADMIN", entry.getValue().getRole());
        assertEquals("STATUS_CHANGED", entry.getValue().getAction());
        assertEquals(1L, entry.getValue().getAccountno());
        assertEquals("ACTIVE -> FROZEN", entry.getValue().getDetail());
    }

    @Test
    void openingAnAccountIsAudited() {
        when(accounts.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        service.open(new AccountRequest(1L, "CURRENT", LocalDate.now(), BigDecimal.ZERO), TELLER);

        ArgumentCaptor<AuditEntry> entry = ArgumentCaptor.forClass(AuditEntry.class);
        verify(audit).save(entry.capture());
        assertEquals("ACCOUNT_OPENED", entry.getValue().getAction());
        assertEquals("teller", entry.getValue().getActor());
    }

    @Test
    void aRefusedChangeLeavesNoAuditLine() {
        account(1, 1, "SAVINGS", "5000.00");

        rejected(() -> service.changeStatus(1L, AccountStatus.CLOSED, ADMIN), HttpStatus.CONFLICT);

        verify(audit, never()).save(any(AuditEntry.class));
    }

    // ---------------- interest ----------------

    @Test
    void interestIsCreditedMonthlyToSavingsAndFixedDepositsButNotCurrentAccounts() {
        Account savings = account(1, 1, "SAVINGS", "120000.00");
        Account current = account(2, 1, "CURRENT", "50000.00");
        Account fixed = account(3, 1, "FIXED_DEPOSIT", "600000.00");
        when(accounts.findByStatus(AccountStatus.ACTIVE)).thenReturn(List.of(savings, current, fixed));

        InterestRunResponse result = service.accrueInterest(YearMonth.of(2026, 10), ADMIN);

        // 120,000 x 3.50% / 12 = 350.00      600,000 x 7.00% / 12 = 3,500.00
        assertEquals(new BigDecimal("120350.00"), savings.getBalance());
        assertEquals(new BigDecimal("50000.00"), current.getBalance());
        assertEquals(new BigDecimal("603500.00"), fixed.getBalance());
        assertEquals("2026-10", result.month());
        assertEquals(2, result.accountsCredited());
        assertEquals(new BigDecimal("3850.00"), result.totalInterest());
        ArgumentCaptor<AccountTransaction> lines = ArgumentCaptor.forClass(AccountTransaction.class);
        verify(ledger, times(2)).save(lines.capture());
        assertEquals(TransactionType.INTEREST, lines.getAllValues().get(0).getType());
        assertEquals("interest-2026-10-1", lines.getAllValues().get(0).getIdempotencyKey());
    }

    @Test
    void runningInterestAgainForTheSameMonthCreditsNothing() {
        Account savings = account(1, 1, "SAVINGS", "120000.00");
        when(accounts.findByStatus(AccountStatus.ACTIVE)).thenReturn(List.of(savings));
        when(ledger.findByIdempotencyKey("interest-2026-10-1")).thenReturn(Optional.of(
                new AccountTransaction(1L, TransactionType.INTEREST, new BigDecimal("350.00"), new BigDecimal("120350.00"),
                        null, "ref", "Interest for 2026-10", "admin", "interest-2026-10-1")));

        InterestRunResponse result = service.accrueInterest(YearMonth.of(2026, 10), ADMIN);

        assertEquals(0, result.accountsCredited());
        assertEquals(new BigDecimal("120000.00"), savings.getBalance());
        verify(ledger, never()).save(any(AccountTransaction.class));
    }

    @Test
    void interestSkipsFrozenAccountsEmptyAccountsAndAmountsUnderOneCent() {
        Account frozen = account(1, 1, "SAVINGS", "120000.00");
        frozen.setStatus(AccountStatus.FROZEN);
        Account tiny = account(2, 1, "SAVINGS", "1.00");     // 1.00 x 3.5% / 12 = 0.0029 -> rounds to 0.00
        when(accounts.findByStatus(AccountStatus.ACTIVE)).thenReturn(List.of(frozen, tiny));

        InterestRunResponse result = service.accrueInterest(YearMonth.of(2026, 10), ADMIN);

        assertEquals(0, result.accountsCredited());
        assertEquals(BigDecimal.ZERO, result.totalInterest());
        assertEquals(new BigDecimal("120000.00"), frozen.getBalance());
    }

    @Test
    void theInterestRunIsAudited() {
        when(accounts.findByStatus(AccountStatus.ACTIVE)).thenReturn(List.of());

        service.accrueInterest(YearMonth.of(2026, 10), ADMIN);

        ArgumentCaptor<AuditEntry> entry = ArgumentCaptor.forClass(AuditEntry.class);
        verify(audit).save(entry.capture());
        assertEquals("INTEREST_RUN", entry.getValue().getAction());
        assertEquals("2026-10: 0 accounts credited, total 0", entry.getValue().getDetail());
    }

    // ---------------- statements by date ----------------

    @Test
    void aStatementCanBeLimitedToADateRangeBothEndsInclusive() {
        account(1, 1, "SAVINGS", "5000.00");
        when(ledger.findStatement(anyLong(), any(), any(), any(Pageable.class))).thenReturn(List.of());

        service.statement(1L, LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31), 20, TELLER);

        verify(ledger).findStatement(org.mockito.ArgumentMatchers.eq(1L),
                org.mockito.ArgumentMatchers.eq(LocalDateTime.of(2026, 10, 1, 0, 0)),
                org.mockito.ArgumentMatchers.eq(LocalDateTime.of(2026, 11, 1, 0, 0)), any(Pageable.class));
    }

    @Test
    void aBackwardsDateRangeIsRejected() {
        account(1, 1, "SAVINGS", "5000.00");

        rejected(() -> service.statement(1L, LocalDate.of(2026, 11, 1), LocalDate.of(2026, 10, 1), 20, TELLER), HttpStatus.BAD_REQUEST);
    }

    @Test
    void auditTrailIsReturnedNewestFirstToAdmins() {
        when(audit.findAllByOrderByIdDesc(any(Pageable.class))).thenReturn(List.of(
                new AuditEntry("admin", "ADMIN", "STATUS_CHANGED", 1L, "ACTIVE -> FROZEN")));

        List<AuditResponse> trail = service.auditTrail(50, ADMIN);

        assertEquals(1, trail.size());
        assertEquals("STATUS_CHANGED", trail.get(0).action());
    }

}
