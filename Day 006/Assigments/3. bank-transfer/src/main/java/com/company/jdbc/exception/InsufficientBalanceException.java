package com.company.jdbc.exception;

/*
Thrown when the source account does not have enough balance.
*/
public class InsufficientBalanceException extends Exception {

    private static final long serialVersionUID = 1L;

    public InsufficientBalanceException(String message) {
        super(message);
    }
}