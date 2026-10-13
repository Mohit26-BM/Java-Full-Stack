package com.company.boot.exception;

import java.math.BigDecimal;

/** -> 422 (unchecked, so a transaction is rolled back) */
public class InsufficientFundsException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InsufficientFundsException(Long accountId, BigDecimal balance, BigDecimal amount) {
        super("Insufficient funds in account " + accountId + ": balance " + balance + ", requested " + amount);
    }
}
