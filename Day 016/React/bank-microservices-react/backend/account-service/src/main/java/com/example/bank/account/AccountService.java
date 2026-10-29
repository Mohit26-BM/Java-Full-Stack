package com.example.bank.account;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * All the money rules and all the access rules. Every method that changes a balance runs in ONE database transaction
 * and locks the account rows first, so a transfer either happens completely or not at all and two requests cannot
 * spend the same money.
 *
 * Who may do what:
 *   CUSTOMER  sees only the accounts of the customer record the login is linked to; may withdraw from and transfer out
 *             of those accounts (up to SELF_SERVICE_LIMIT per transaction); cannot deposit or open accounts.
 *   TELLER    works with any account: open, deposit, withdraw, transfer, statements.
 *   ADMIN     everything a teller can, plus status changes, interest runs and the audit log.
 * A request without a role is refused. A customer asking for somebody else's account is told it does not exist.
 */
@Service
@Transactional(readOnly = true)
public class AccountService {

    /** The most a customer can withdraw or transfer in one go through self-service; bigger amounts need a branch. */
    public static final BigDecimal SELF_SERVICE_LIMIT = new BigDecimal("100000.00");

    private static final Logger log = LoggerFactory.getLogger(AccountService.class);
    private static final EnumSet<TransactionType> OUTGOING = EnumSet.of(TransactionType.WITHDRAWAL, TransactionType.TRANSFER_OUT);

    private final AccountRepository accounts;
    private final AccountTransactionRepository ledger;
    private final AuditEntryRepository audit;

    public AccountService(AccountRepository accounts, AccountTransactionRepository ledger, AuditEntryRepository audit) {
        this.accounts = accounts;
        this.ledger = ledger;
        this.audit = audit;
    }

    // ---------------- reading ----------------

    /** Staff see every account; a customer sees their own (an unlinked customer login sees none). */
    public List<AccountResponse> list(Caller caller) {
        if (caller.isStaff()) {
            return accounts.findAll().stream().map(AccountResponse::from).collect(Collectors.toList());
        }
        if (caller.isCustomer()) {
            if (caller.customerId() == null) {
                return List.of();
            }
            return accounts.findByCustomerId(caller.customerId()).stream().map(AccountResponse::from).collect(Collectors.toList());
        }
        throw denied(caller, "list accounts", "Access denied");
    }

    public AccountResponse get(Long accountno, Caller caller) {
        Account account = find(accountno);
        requireAccess(caller, account);
        return AccountResponse.from(account);
    }

    /**
     * Newest first. {@code from} and {@code to} are optional dates (both inclusive); {@code limit} is capped at 100.
     */
    public List<TransactionResponse> statement(Long accountno, LocalDate from, LocalDate to, int limit, Caller caller) {
        Account account = find(accountno);
        requireAccess(caller, account);
        if (from != null && to != null && from.isAfter(to)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "'from' must not be after 'to'");
        }
        LocalDateTime start = from == null ? null : from.atStartOfDay();
        LocalDateTime end = to == null ? null : to.plusDays(1).atStartOfDay();
        return ledger.findStatement(accountno, start, end, PageRequest.of(0, Math.min(Math.max(limit, 1), 100)))
                .stream().map(TransactionResponse::from).collect(Collectors.toList());
    }

    // ---------------- opening ----------------

    @Transactional
    public AccountResponse open(AccountRequest request, Caller caller) {
        requireStaff(caller, "open accounts", "Only bank staff can open accounts");
        AccountType type = AccountType.valueOf(request.accountType());
        if (request.balance().compareTo(type.minimumOpeningBalance()) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A " + type + " account needs an opening balance of at least "
                    + type.minimumOpeningBalance());
        }
        Account saved = accounts.save(new Account(request.customerId(), request.accountType(), request.openingDate(),
                request.balance()));
        if (saved.getBalance().signum() > 0) {
            ledger.save(new AccountTransaction(saved.getAccountno(), TransactionType.DEPOSIT, saved.getBalance(),
                    saved.getBalance(), null, UUID.randomUUID().toString(), "Opening balance", caller.username(), null));
        }
        record(caller, "ACCOUNT_OPENED", saved.getAccountno(),
                type + " for customer " + request.customerId() + ", opening balance " + saved.getBalance());
        return AccountResponse.from(saved);
    }

    // ---------------- money movements ----------------

    /** Deposits are made over the counter, so only staff can make them. */
    @Transactional
    public TransactionResponse deposit(Long accountno, AmountRequest request, String idempotencyKey, Caller caller) {
        requireStaff(caller, "deposit", "Only bank staff can record deposits; please visit a branch");
        Optional<TransactionResponse> replay = replay(idempotencyKey, accountno, TransactionType.DEPOSIT, request.amount());
        if (replay.isPresent()) {
            return replay.get();
        }
        Account account = lock(accountno);
        requireActive(account);
        if (!account.type().acceptsDeposits()) {
            throw conflict("A " + account.type() + " account does not accept deposits");
        }
        account.setBalance(account.getBalance().add(request.amount()));
        return TransactionResponse.from(ledger.save(new AccountTransaction(accountno, TransactionType.DEPOSIT,
                request.amount(), account.getBalance(), null, UUID.randomUUID().toString(), request.description(),
                caller.username(), idempotencyKey)));
    }

    @Transactional
    public TransactionResponse withdraw(Long accountno, AmountRequest request, String idempotencyKey, Caller caller) {
        Optional<TransactionResponse> replay = replay(idempotencyKey, accountno, TransactionType.WITHDRAWAL, request.amount());
        if (replay.isPresent()) {
            requireAccess(caller, find(accountno));
            return replay.get();
        }
        Account account = lock(accountno);
        requireAccess(caller, account);
        requireSelfServiceLimit(caller, request.amount());
        requireCanSpend(account, request.amount());
        account.setBalance(account.getBalance().subtract(request.amount()));
        return TransactionResponse.from(ledger.save(new AccountTransaction(accountno, TransactionType.WITHDRAWAL,
                request.amount(), account.getBalance(), null, UUID.randomUUID().toString(), request.description(),
                caller.username(), idempotencyKey)));
    }

    @Transactional
    public TransactionResponse transfer(TransferRequest request, String idempotencyKey, Caller caller) {
        if (request.fromAccount().equals(request.toAccount())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot transfer to the same account");
        }
        Optional<TransactionResponse> replay = replay(idempotencyKey, request.fromAccount(), TransactionType.TRANSFER_OUT,
                request.amount());
        if (replay.isPresent()) {
            requireAccess(caller, find(request.fromAccount()));
            return replay.get();
        }
        // lock in a fixed order (lowest account number first) so two opposite transfers cannot deadlock
        Account first = lock(Math.min(request.fromAccount(), request.toAccount()));
        Account second = lock(Math.max(request.fromAccount(), request.toAccount()));
        Account from = first.getAccountno().equals(request.fromAccount()) ? first : second;
        Account to = from == first ? second : first;

        requireAccess(caller, from); // the money must come from an account the caller may use
        requireSelfServiceLimit(caller, request.amount());
        requireCanSpend(from, request.amount());
        requireActive(to);
        if (!to.type().acceptsDeposits()) {
            throw conflict("A " + to.type() + " account does not accept deposits, so account " + to.getAccountno()
                    + " cannot receive a transfer");
        }
        from.setBalance(from.getBalance().subtract(request.amount()));
        to.setBalance(to.getBalance().add(request.amount()));

        String reference = UUID.randomUUID().toString();
        AccountTransaction out = ledger.save(new AccountTransaction(from.getAccountno(), TransactionType.TRANSFER_OUT,
                request.amount(), from.getBalance(), to.getAccountno(), reference, request.description(),
                caller.username(), idempotencyKey));
        ledger.save(new AccountTransaction(to.getAccountno(), TransactionType.TRANSFER_IN, request.amount(),
                to.getBalance(), from.getAccountno(), reference, request.description(), caller.username(), null));
        return TransactionResponse.from(out);
    }

    // ---------------- administration ----------------

    @Transactional
    public AccountResponse changeStatus(Long accountno, AccountStatus newStatus, Caller caller) {
        requireAdmin(caller, "change account status");
        Account account = lock(accountno);
        if (account.getStatus() == AccountStatus.CLOSED) {
            throw conflict("Account " + accountno + " is closed and cannot be changed");
        }
        if (newStatus == AccountStatus.CLOSED && account.getBalance().signum() != 0) {
            throw conflict("Only an account with a zero balance can be closed (balance is " + account.getBalance() + ")");
        }
        AccountStatus before = account.getStatus();
        account.setStatus(newStatus);
        record(caller, "STATUS_CHANGED", accountno, before + " -> " + newStatus);
        return AccountResponse.from(account);
    }

    /**
     * Credits one month of interest to every ACTIVE account whose type pays it (rate / 12 of the current balance,
     * rounded to the cent). Safe to run twice for the same month: each credit carries the key
     * "interest-YYYY-MM-accountno", so an account is never credited twice for a month.
     */
    @Transactional
    public InterestRunResponse accrueInterest(YearMonth month, Caller caller) {
        requireAdmin(caller, "run interest");
        int credited = 0;
        BigDecimal total = BigDecimal.ZERO;
        for (Account listed : accounts.findByStatus(AccountStatus.ACTIVE)) {
            BigDecimal rate = listed.type().annualInterestRate();
            String key = "interest-" + month + "-" + listed.getAccountno();
            if (rate.signum() == 0 || ledger.findByIdempotencyKey(key).isPresent()) {
                continue;
            }
            Account account = lock(listed.getAccountno()); // re-read under the lock: the balance may have just changed
            if (account.getStatus() != AccountStatus.ACTIVE || account.getBalance().signum() <= 0) {
                continue;
            }
            BigDecimal interest = account.getBalance().multiply(rate).divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_EVEN);
            if (interest.signum() == 0) {
                continue;
            }
            account.setBalance(account.getBalance().add(interest));
            ledger.save(new AccountTransaction(account.getAccountno(), TransactionType.INTEREST, interest,
                    account.getBalance(), null, UUID.randomUUID().toString(), "Interest for " + month,
                    caller.username(), key));
            credited++;
            total = total.add(interest);
        }
        record(caller, "INTEREST_RUN", null, month + ": " + credited + " accounts credited, total " + total);
        return new InterestRunResponse(month.toString(), credited, total);
    }

    /** Newest first, admins only. */
    public List<AuditResponse> auditTrail(int limit, Caller caller) {
        requireAdmin(caller, "read the audit log");
        return audit.findAllByOrderByIdDesc(PageRequest.of(0, Math.min(Math.max(limit, 1), 200)))
                .stream().map(AuditResponse::from).collect(Collectors.toList());
    }

    // ---------------- rules ----------------

    private void requireActive(Account account) {
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw conflict("Account " + account.getAccountno() + " is " + account.getStatus());
        }
    }

    /** Active, the type allows money to leave, the minimum balance is kept, and today's limit is not exceeded. */
    private void requireCanSpend(Account account, BigDecimal amount) {
        requireActive(account);
        AccountType type = account.type();
        if (!type.allowsWithdrawals()) {
            throw conflict("A " + type + " account does not allow withdrawals or transfers out");
        }
        if (account.getBalance().subtract(amount).compareTo(type.minimumBalance()) < 0) {
            throw conflict("Insufficient funds: a " + type + " account must keep a balance of at least "
                    + type.minimumBalance() + " (balance is " + account.getBalance() + ")");
        }
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        BigDecimal spentToday = ledger.sumSince(account.getAccountno(), OUTGOING, startOfDay);
        if (spentToday.add(amount).compareTo(type.dailyOutgoingLimit()) > 0) {
            throw conflict("Daily limit exceeded: a " + type + " account can send at most " + type.dailyOutgoingLimit()
                    + " per day (already sent today: " + spentToday + ")");
        }
    }

    /** Customers using self-service are limited per transaction; staff handle larger amounts at the branch. */
    private void requireSelfServiceLimit(Caller caller, BigDecimal amount) {
        if (caller.isCustomer() && amount.compareTo(SELF_SERVICE_LIMIT) > 0) {
            throw conflict("Amounts above " + SELF_SERVICE_LIMIT + " cannot be done in self-service; please visit a branch");
        }
    }

    /** A retried request with the same Idempotency-Key returns the original result instead of moving money again. */
    private Optional<TransactionResponse> replay(String key, Long accountno, TransactionType type, BigDecimal amount) {
        if (key == null) {
            return Optional.empty();
        }
        return ledger.findByIdempotencyKey(key).map(existing -> {
            boolean same = existing.getAccountno().equals(accountno) && existing.getType() == type
                    && existing.getAmount().compareTo(amount) == 0;
            if (!same) {
                throw conflict("This Idempotency-Key was already used for a different request");
            }
            return TransactionResponse.from(existing);
        });
    }

    // ---------------- access ----------------

    private void requireStaff(Caller caller, String what, String message) {
        if (!caller.isStaff()) {
            throw denied(caller, what, message);
        }
    }

    private void requireAdmin(Caller caller, String what) {
        if (!caller.isAdmin()) {
            throw denied(caller, what, "Only an administrator can do this");
        }
    }

    /** Staff may use any account; a customer only their own. Others get "does not exist", so account numbers cannot be probed. */
    private void requireAccess(Caller caller, Account account) {
        if (caller.isStaff() || caller.owns(account.getCustomerId())) {
            return;
        }
        if (caller.isCustomer()) {
            log.warn("Customer {} (customer id {}) asked for account {} which is not theirs", caller.username(),
                    caller.customerId(), account.getAccountno());
            throw notFound(account.getAccountno());
        }
        throw denied(caller, "use account " + account.getAccountno(), "Access denied");
    }

    private ResponseStatusException denied(Caller caller, String what, String message) {
        log.warn("Access denied: {} (role {}) tried to {}", caller.username(), caller.role(), what);
        return new ResponseStatusException(HttpStatus.FORBIDDEN, message);
    }

    private void record(Caller caller, String action, Long accountno, String detail) {
        audit.save(new AuditEntry(caller.username(), caller.role(), action, accountno, detail));
    }

    private Account find(Long accountno) {
        return accounts.findById(accountno).orElseThrow(() -> notFound(accountno));
    }

    private Account lock(Long accountno) {
        return accounts.findForUpdate(accountno).orElseThrow(() -> notFound(accountno));
    }

    private ResponseStatusException notFound(Long accountno) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Account " + accountno + " does not exist");
    }

    private ResponseStatusException conflict(String message) {
        return new ResponseStatusException(HttpStatus.CONFLICT, message);
    }
}
