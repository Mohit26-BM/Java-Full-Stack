package com.example.bank.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accounts;
    @Mock
    private AccountTransactionRepository ledger;
    @Mock
    private AuditEntryRepository audit;

    private static final Caller STAFF = new Caller("mohit", "TELLER", null);
    private static final Caller ADMIN = new Caller("admin", "ADMIN", null);

    private AccountService service;

    @BeforeEach
    void setUp() {
        service = new AccountService(accounts, ledger, audit);
        lenient().when(ledger.sumSince(anyLong(), any(), any())).thenReturn(BigDecimal.ZERO);
        lenient().when(ledger.save(any(AccountTransaction.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private Account account(long id, String type, String balance) {
        Account a = new Account(1L, type, LocalDate.of(2024, 1, 1), new BigDecimal(balance));
        ReflectionTestUtils.setField(a, "accountno", id);
        lenient().when(accounts.findForUpdate(id)).thenReturn(Optional.of(a));
        lenient().when(accounts.findById(id)).thenReturn(Optional.of(a));
        return a;
    }

    private AmountRequest amount(String value) {
        return new AmountRequest(new BigDecimal(value), "test");
    }

    private ResponseStatusException rejected(Runnable action, HttpStatus status) {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, action::run);
        assertEquals(status, ex.getStatusCode());
        return ex;
    }

    // ---------------- deposit ----------------

    @Test
    void depositAddsToTheBalanceAndWritesALedgerLine() {
        Account savings = account(1, "SAVINGS", "5000.00");

        TransactionResponse result = service.deposit(1L, amount("250.50"), null, STAFF);

        assertEquals(new BigDecimal("5250.50"), savings.getBalance());
        assertEquals("DEPOSIT", result.type());
        assertEquals(new BigDecimal("5250.50"), result.balanceAfter());
        assertEquals("mohit", result.performedBy());
    }

    @Test
    void depositIntoAFrozenAccountIsRejected() {
        Account frozen = account(1, "SAVINGS", "5000.00");
        frozen.setStatus(AccountStatus.FROZEN);

        ResponseStatusException ex = rejected(() -> service.deposit(1L, amount("10"), null, STAFF), HttpStatus.CONFLICT);

        assertEquals("Account 1 is FROZEN", ex.getReason());
        verify(ledger, never()).save(any(AccountTransaction.class));
    }

    @Test
    void fixedDepositAccountsDoNotAcceptDeposits() {
        account(1, "FIXED_DEPOSIT", "50000.00");

        rejected(() -> service.deposit(1L, amount("10"), null, STAFF), HttpStatus.CONFLICT);
    }

    @Test
    void depositToAnUnknownAccountGives404() {
        when(accounts.findForUpdate(99L)).thenReturn(Optional.empty());

        rejected(() -> service.deposit(99L, amount("10"), null, STAFF), HttpStatus.NOT_FOUND);
    }

    // ---------------- withdraw ----------------

    @Test
    void withdrawSubtractsFromTheBalance() {
        Account savings = account(1, "SAVINGS", "5000.00");

        TransactionResponse result = service.withdraw(1L, amount("1500"), null, STAFF);

        assertEquals(new BigDecimal("3500.00"), savings.getBalance());
        assertEquals("WITHDRAWAL", result.type());
    }

    @Test
    void savingsMustKeepTheMinimumBalance() {
        Account savings = account(1, "SAVINGS", "1500.00");

        ResponseStatusException ex = rejected(() -> service.withdraw(1L, amount("600"), null, STAFF), HttpStatus.CONFLICT);

        assertEquals(new BigDecimal("1500.00"), savings.getBalance());
        assertEquals(true, ex.getReason().startsWith("Insufficient funds"));
        verify(ledger, never()).save(any(AccountTransaction.class));
    }

    @Test
    void currentAccountsMayOverdrawUpToTheLimitButNotBeyond() {
        Account current = account(1, "CURRENT", "5000.00");

        service.withdraw(1L, amount("15000"), null, STAFF);
        assertEquals(new BigDecimal("-10000.00"), current.getBalance());

        rejected(() -> service.withdraw(1L, amount("0.01"), null, STAFF), HttpStatus.CONFLICT);
    }

    @Test
    void fixedDepositAccountsDoNotAllowWithdrawals() {
        account(1, "FIXED_DEPOSIT", "50000.00");

        rejected(() -> service.withdraw(1L, amount("1"), null, STAFF), HttpStatus.CONFLICT);
    }

    @Test
    void theDailyLimitCountsWhatWasAlreadySentToday() {
        account(1, "SAVINGS", "900000.00");
        when(ledger.sumSince(anyLong(), any(), any())).thenReturn(new BigDecimal("45000.00"));

        ResponseStatusException ex = rejected(() -> service.withdraw(1L, amount("5000.01"), null, STAFF), HttpStatus.CONFLICT);
        assertEquals(true, ex.getReason().startsWith("Daily limit exceeded"));

        service.withdraw(1L, amount("5000.00"), null, STAFF);
    }

    // ---------------- transfer ----------------

    @Test
    void transferMovesMoneyAndWritesTwoLinesWithTheSameReference() {
        Account from = account(1, "SAVINGS", "10000.00");
        Account to = account(2, "CURRENT", "100.00");

        TransactionResponse result = service.transfer(new TransferRequest(1L, 2L, new BigDecimal("2500"), "rent"), null, STAFF);

        assertEquals(new BigDecimal("7500.00"), from.getBalance());
        assertEquals(new BigDecimal("2600.00"), to.getBalance());
        assertEquals("TRANSFER_OUT", result.type());
        ArgumentCaptor<AccountTransaction> saved = ArgumentCaptor.forClass(AccountTransaction.class);
        verify(ledger, times(2)).save(saved.capture());
        List<AccountTransaction> lines = saved.getAllValues();
        assertEquals(TransactionType.TRANSFER_OUT, lines.get(0).getType());
        assertEquals(TransactionType.TRANSFER_IN, lines.get(1).getType());
        assertEquals(lines.get(0).getReference(), lines.get(1).getReference());
        assertEquals(2L, lines.get(0).getRelatedAccountno());
        assertEquals(1L, lines.get(1).getRelatedAccountno());
    }

    @Test
    void transferToTheSameAccountIsRejected() {
        rejected(() -> service.transfer(new TransferRequest(1L, 1L, BigDecimal.TEN, null), null, STAFF), HttpStatus.BAD_REQUEST);
    }

    @Test
    void transferWithInsufficientFundsChangesNothing() {
        Account from = account(1, "SAVINGS", "1200.00");
        Account to = account(2, "SAVINGS", "5000.00");

        rejected(() -> service.transfer(new TransferRequest(1L, 2L, new BigDecimal("500"), null), null, STAFF), HttpStatus.CONFLICT);

        assertEquals(new BigDecimal("1200.00"), from.getBalance());
        assertEquals(new BigDecimal("5000.00"), to.getBalance());
        verify(ledger, never()).save(any(AccountTransaction.class));
    }

    @Test
    void transferToAFrozenAccountChangesNothing() {
        Account from = account(1, "SAVINGS", "10000.00");
        Account to = account(2, "SAVINGS", "5000.00");
        to.setStatus(AccountStatus.FROZEN);

        rejected(() -> service.transfer(new TransferRequest(1L, 2L, new BigDecimal("100"), null), null, STAFF), HttpStatus.CONFLICT);

        assertEquals(new BigDecimal("10000.00"), from.getBalance());
        assertEquals(new BigDecimal("5000.00"), to.getBalance());
    }

    @Test
    void transferIntoAFixedDepositIsRejected() {
        account(1, "SAVINGS", "10000.00");
        account(2, "FIXED_DEPOSIT", "50000.00");

        rejected(() -> service.transfer(new TransferRequest(1L, 2L, new BigDecimal("100"), null), null, STAFF), HttpStatus.CONFLICT);
    }

    @Test
    void accountsAreLockedLowestNumberFirstWhateverTheDirection() {
        account(5, "SAVINGS", "10000.00");
        account(3, "SAVINGS", "10000.00");

        service.transfer(new TransferRequest(5L, 3L, new BigDecimal("100"), null), null, STAFF);

        org.mockito.InOrder order = org.mockito.Mockito.inOrder(accounts);
        order.verify(accounts).findForUpdate(3L);
        order.verify(accounts).findForUpdate(5L);
    }

    // ---------------- retries ----------------

    @Test
    void aRetriedRequestWithTheSameKeyReturnsTheFirstResultWithoutMovingMoneyAgain() {
        Account savings = account(1, "SAVINGS", "5000.00");
        AccountTransaction first = new AccountTransaction(1L, TransactionType.WITHDRAWAL, new BigDecimal("100.00"),
                new BigDecimal("4900.00"), null, "ref-1", "test", "mohit", "key-1");
        when(ledger.findByIdempotencyKey("key-1")).thenReturn(Optional.of(first));

        TransactionResponse result = service.withdraw(1L, amount("100"), "key-1", STAFF);

        assertEquals("ref-1", result.reference());
        assertEquals(new BigDecimal("5000.00"), savings.getBalance());
        verify(accounts, never()).findForUpdate(anyLong());
        verify(ledger, never()).save(any(AccountTransaction.class));
    }

    @Test
    void reusingAKeyForADifferentRequestIsRejected() {
        AccountTransaction first = new AccountTransaction(1L, TransactionType.WITHDRAWAL, new BigDecimal("100.00"),
                new BigDecimal("4900.00"), null, "ref-1", "test", "mohit", "key-1");
        when(ledger.findByIdempotencyKey("key-1")).thenReturn(Optional.of(first));

        rejected(() -> service.withdraw(1L, amount("999"), "key-1", STAFF), HttpStatus.CONFLICT);
        rejected(() -> service.deposit(1L, amount("100"), "key-1", STAFF), HttpStatus.CONFLICT);
    }

    // ---------------- status ----------------

    @Test
    void anAccountWithMoneyCannotBeClosed() {
        account(1, "SAVINGS", "1000.00");

        rejected(() -> service.changeStatus(1L, AccountStatus.CLOSED, ADMIN), HttpStatus.CONFLICT);
    }

    @Test
    void anAccountWithAZeroBalanceCanBeClosedAndThenStaysClosed() {
        Account current = account(1, "CURRENT", "0.00");

        AccountResponse closed = service.changeStatus(1L, AccountStatus.CLOSED, ADMIN);

        assertEquals("CLOSED", closed.status());
        assertEquals(AccountStatus.CLOSED, current.getStatus());
        rejected(() -> service.changeStatus(1L, AccountStatus.ACTIVE, ADMIN), HttpStatus.CONFLICT);
    }

    @Test
    void anAccountCanBeFrozenAndReactivated() {
        Account savings = account(1, "SAVINGS", "5000.00");

        service.changeStatus(1L, AccountStatus.FROZEN, ADMIN);
        assertEquals(AccountStatus.FROZEN, savings.getStatus());
        service.changeStatus(1L, AccountStatus.ACTIVE, ADMIN);
        assertEquals(AccountStatus.ACTIVE, savings.getStatus());
    }

    // ---------------- opening and statement ----------------

    @Test
    void openingBalanceMustMeetTheMinimumForTheType() {
        AccountRequest tooLittle = new AccountRequest(1L, "SAVINGS", LocalDate.now(), new BigDecimal("999.99"));

        ResponseStatusException ex = rejected(() -> service.open(tooLittle, STAFF), HttpStatus.BAD_REQUEST);

        assertEquals("A SAVINGS account needs an opening balance of at least 1000.00", ex.getReason());
        verify(accounts, never()).save(any(Account.class));
    }

    @Test
    void openingAnAccountWithMoneyWritesAnOpeningLedgerLine() {
        when(accounts.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        AccountResponse opened = service.open(new AccountRequest(1L, "FIXED_DEPOSIT", LocalDate.now(), new BigDecimal("20000")), STAFF);

        assertEquals("ACTIVE", opened.status());
        ArgumentCaptor<AccountTransaction> line = ArgumentCaptor.forClass(AccountTransaction.class);
        verify(ledger).save(line.capture());
        assertEquals("Opening balance", line.getValue().getDescription());
        assertEquals(new BigDecimal("20000"), line.getValue().getAmount());
    }

    @Test
    void openingACurrentAccountWithZeroWritesNoLedgerLine() {
        when(accounts.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        service.open(new AccountRequest(1L, "CURRENT", LocalDate.now(), BigDecimal.ZERO), STAFF);

        verify(ledger, never()).save(any(AccountTransaction.class));
    }

    @Test
    void theStatementIsCappedAt100LinesAndNeedsARealAccount() {
        account(1, "SAVINGS", "5000.00");
        when(ledger.findStatement(anyLong(), any(), any(), any(Pageable.class))).thenReturn(List.of());

        service.statement(1L, null, null, 500, STAFF);

        verify(ledger).findStatement(1L, null, null, PageRequest.of(0, 100));
        when(accounts.findById(99L)).thenReturn(Optional.empty());
        rejected(() -> service.statement(99L, null, null, 20, STAFF), HttpStatus.NOT_FOUND);
    }

}