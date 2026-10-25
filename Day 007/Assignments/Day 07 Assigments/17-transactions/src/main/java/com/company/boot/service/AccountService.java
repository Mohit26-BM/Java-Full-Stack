package com.company.boot.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.boot.dto.AccountsView;
import com.company.boot.dto.AccountsView.AccountLine;
import com.company.boot.entity.Account;
import com.company.boot.repository.AccountRepository;

/** Reading balances and resetting them between experiments. */
@Service
public class AccountService {

    /** Starting balances: total = 15000.00 */
    private static final Map<Long, BigDecimal> INITIAL_BALANCES = Map.of(
            1L, new BigDecimal("10000.00"),
            2L, new BigDecimal("5000.00"));

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional(readOnly = true)
    public AccountsView findAll() {
        List<AccountLine> lines = accountRepository.findAll(Sort.by("id")).stream()
                .map(a -> new AccountLine(a.getId(), a.getHolderName(), a.getBalance()))
                .toList();
        BigDecimal total = lines.stream().map(AccountLine::balance).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new AccountsView(lines, total);
    }

    @Transactional
    public AccountsView reset() {
        for (Account account : accountRepository.findAll()) {
            BigDecimal initial = INITIAL_BALANCES.get(account.getId());
            if (initial != null) {
                account.setBalance(initial);
            }
        }
        accountRepository.flush();
        return findAll();
    }
}
