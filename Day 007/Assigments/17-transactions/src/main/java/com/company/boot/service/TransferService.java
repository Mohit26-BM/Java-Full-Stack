package com.company.boot.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.boot.entity.Account;
import com.company.boot.exception.AccountNotFoundException;
import com.company.boot.exception.InsufficientFundsException;
import com.company.boot.exception.SimulatedFailureException;
import com.company.boot.exception.TransferException;
import com.company.boot.repository.AccountRepository;

/**
 * Money transfer = DEBIT one account + CREDIT another.
 * Both must happen, or neither (atomicity).
 *
 * Every public method runs the SAME steps (debitThenCredit); only the
 * transaction setup differs, so the results can be compared directly.
 *
 * There is deliberately NO class-level @Transactional - otherwise the
 * "without transaction" methods would get one too.
 */
@Service
public class TransferService {

    private static final Logger log = LoggerFactory.getLogger("TRANSFER");

    private enum Failure { NONE, UNCHECKED, CHECKED }

    private final AccountRepository accountRepository;

    public TransferService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    // ------------------------------------------------------------------
    // 1. The correct way: one transaction around debit + credit.
    //    An unchecked exception -> Spring rolls BOTH changes back.
    // ------------------------------------------------------------------
    @Transactional
    public void transferWithTransaction(Long fromId, Long toId, BigDecimal amount, boolean fail)
            throws TransferException {
        debitThenCredit(fromId, toId, amount, fail ? Failure.UNCHECKED : Failure.NONE);
    }

    // ------------------------------------------------------------------
    // 2. No @Transactional: each repository.save() commits on its own.
    //    The debit is already committed when the exception is thrown.
    // ------------------------------------------------------------------
    public void transferWithoutTransaction(Long fromId, Long toId, BigDecimal amount, boolean fail)
            throws TransferException {
        debitThenCredit(fromId, toId, amount, fail ? Failure.UNCHECKED : Failure.NONE);
    }

    // ------------------------------------------------------------------
    // 3. OPTIONAL EXTENSION: a CHECKED exception with the default rules.
    //    Spring rolls back only for RuntimeException and Error -> this COMMITS.
    // ------------------------------------------------------------------
    @Transactional
    public void transferCheckedDefault(Long fromId, Long toId, BigDecimal amount, boolean fail)
            throws TransferException {
        debitThenCredit(fromId, toId, amount, fail ? Failure.CHECKED : Failure.NONE);
    }

    // ------------------------------------------------------------------
    // 4. OPTIONAL EXTENSION: the same CHECKED exception, but declared as a
    //    rollback reason -> ROLLBACK.
    // ------------------------------------------------------------------
    @Transactional(rollbackFor = TransferException.class)
    public void transferCheckedRollbackFor(Long fromId, Long toId, BigDecimal amount, boolean fail)
            throws TransferException {
        debitThenCredit(fromId, toId, amount, fail ? Failure.CHECKED : Failure.NONE);
    }

    // ------------------------------------------------------------------
    // 5. Placement pitfall: this method has no @Transactional and calls a
    //    @Transactional method on "this". The call does not go through the
    //    Spring proxy, so NO transaction is started.
    // ------------------------------------------------------------------
    public void transferSelfInvocation(Long fromId, Long toId, BigDecimal amount, boolean fail)
            throws TransferException {
        log.info("self-invocation: calling this.transferWithTransaction(...) directly");
        this.transferWithTransaction(fromId, toId, amount, fail);
    }

    // ------------------------------------------------------------------
    // 6. Pitfall: the exception is caught INSIDE the transaction. Spring never
    //    sees it, so it commits whatever was done - the half-finished transfer.
    // ------------------------------------------------------------------
    @Transactional
    public void transferSwallowingException(Long fromId, Long toId, BigDecimal amount, boolean fail)
            throws TransferException {
        try {
            debitThenCredit(fromId, toId, amount, fail ? Failure.UNCHECKED : Failure.NONE);
        } catch (SimulatedFailureException ex) {
            log.warn("caught and ignored: {}", ex.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // The shared steps
    // ------------------------------------------------------------------
    private void debitThenCredit(Long fromId, Long toId, BigDecimal amount, Failure failure)
            throws TransferException {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        if (fromId == null || toId == null || fromId.equals(toId)) {
            throw new IllegalArgumentException("Two different accounts are required");
        }
        Account from = accountRepository.findById(fromId).orElseThrow(() -> new AccountNotFoundException(fromId));
        Account to = accountRepository.findById(toId).orElseThrow(() -> new AccountNotFoundException(toId));
        if (from.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(fromId, from.getBalance(), amount);
        }

        // STEP 1: debit (saveAndFlush = the UPDATE is really sent to MySQL now,
        // so a rollback has to UNDO an executed statement)
        from.setBalance(from.getBalance().subtract(amount));
        accountRepository.saveAndFlush(from);
        log.info("STEP 1 debit  account {} -{} -> {}", fromId, amount, from.getBalance());

        // --- the intentional failure BETWEEN the two operations ---
        if (failure == Failure.UNCHECKED) {
            log.info("throwing UNCHECKED SimulatedFailureException before the credit");
            throw new SimulatedFailureException("Simulated failure after debit, before credit");
        }
        if (failure == Failure.CHECKED) {
            log.info("throwing CHECKED TransferException before the credit");
            throw new TransferException("Simulated checked failure after debit, before credit");
        }

        // STEP 2: credit
        to.setBalance(to.getBalance().add(amount));
        accountRepository.saveAndFlush(to);
        log.info("STEP 2 credit account {} +{} -> {}", toId, amount, to.getBalance());
    }
}
