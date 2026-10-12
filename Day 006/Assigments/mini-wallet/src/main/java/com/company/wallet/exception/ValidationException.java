package com.company.wallet.exception;

/*
Input failed validation before reaching the database.
*/
public class ValidationException extends WalletException {

    private static final long serialVersionUID = 1L;

    public ValidationException(String message) {
        super(message);
    }
}