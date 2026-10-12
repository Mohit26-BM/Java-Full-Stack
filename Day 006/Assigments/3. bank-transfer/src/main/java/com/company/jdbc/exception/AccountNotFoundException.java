package com.company.jdbc.exception;

/*
Thrown when no account exists for the given account number.
*/
public class AccountNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    public AccountNotFoundException(String message) {
        super(message);
    }
}