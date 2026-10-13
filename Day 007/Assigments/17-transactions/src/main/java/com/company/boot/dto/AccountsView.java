package com.company.boot.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * All accounts + the sum of all balances.
 * Transfers only MOVE money, so totalBalance must always stay the same.
 * If it changes, money was created or destroyed -> a broken (non-atomic) transfer.
 */
public record AccountsView(List<AccountLine> accounts, BigDecimal totalBalance) {

    public record AccountLine(Long id, String holderName, BigDecimal balance) {
    }
}
