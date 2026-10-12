package com.company.jdbc.exception;

/*
Thrown when an account involved in a transfer is not ACTIVE.
*/
public class AccountInactiveException extends Exception {

    private static final long serialVersionUID = 1L;

    public AccountInactiveException(String message) {
        super(message);
    }
}