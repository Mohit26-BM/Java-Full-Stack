package com.company.boot.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.boot.dto.AccountRequest;
import com.company.boot.exception.InsufficientFundsException;
import com.company.boot.exception.ResourceNotFoundException;
import com.company.boot.model.Account;
import com.company.boot.repository.AccountRepository;

/**
 * Business logic. It only THROWS exceptions - it never decides HTTP status
 * codes or builds error responses (that is GlobalExceptionHandler's job).
 */
@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    public Account findById(int accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId));
    }

    public Account create(AccountRequest request) {
        int id = accountRepository.insert(request.getHolderName().trim(),
                request.getEmail().trim().toLowerCase(), request.getOpeningBalance());
        return findById(id);
    }

    @Transactional
    public Account withdraw(int accountId, BigDecimal amount) {
        requirePositive(amount);
        Account account = lockAccount(accountId);
        if (account.balance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(accountId, account.balance(), amount);
        }
        accountRepository.updateBalance(accountId, account.balance().subtract(amount));
        return findById(accountId);
    }

    /** Moves money between two accounts in ONE transaction (rolls back on any exception). */
    @Transactional
    public List<Account> transfer(int fromId, int toId, BigDecimal amount) {
        if (fromId == toId) {
            throw new IllegalArgumentException("Source and target account must be different");
        }
        requirePositive(amount);

        // lock in id order to avoid deadlocks between opposite transfers
        Account first = lockAccount(Math.min(fromId, toId));
        Account second = lockAccount(Math.max(fromId, toId));
        Account from = first.accountId() == fromId ? first : second;
        Account to = first.accountId() == toId ? first : second;

        if (from.balance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(fromId, from.balance(), amount);
        }
        accountRepository.updateBalance(fromId, from.balance().subtract(amount));
        accountRepository.updateBalance(toId, to.balance().add(amount));
        return List.of(findById(fromId), findById(toId));
    }

    /**
     * Monthly instalment = balance / months.
     * months = 0 is NOT checked on purpose: it triggers an unexpected
     * ArithmeticException to demonstrate the 500 safety-net handler.
     */
    public BigDecimal monthlyInstalment(int accountId, int months) {
        Account account = findById(accountId);
        return account.balance().divide(BigDecimal.valueOf(months), 2, java.math.RoundingMode.HALF_UP);
    }

    private Account lockAccount(int accountId) {
        return accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId));
    }

    private void requirePositive(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0 (got " + amount + ")");
        }
        if (amount.scale() > 2) {
            throw new IllegalArgumentException("Amount may have at most 2 decimal places (got " + amount + ")");
        }
    }
}
