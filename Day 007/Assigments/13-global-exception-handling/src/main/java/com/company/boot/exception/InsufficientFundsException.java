package com.company.boot.exception;

import java.math.BigDecimal;

/**
 * The request is valid but the account cannot cover it -> 422 Unprocessable Entity.
 */
public class InsufficientFundsException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InsufficientFundsException(int accountId, BigDecimal balance, BigDecimal requested) {
        super("Insufficient funds in account " + accountId + ": balance " + balance
                + ", requested " + requested);
    }
}
