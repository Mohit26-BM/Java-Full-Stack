package com.company.wallet.exception;

/*
Unchecked wrapper for unexpected SQLExceptions outside a business
transaction (database down, pool timeout, bad SQL ...).
Handled centrally by app/ErrorHandler as a technical error.
*/
public class DataAccessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}