package com.company.jdbc.exception;

/*
Thrown when the account number already exists (UNIQUE constraint).
*/
public class DuplicateAccountException extends Exception {

    private static final long serialVersionUID = 1L;

    public DuplicateAccountException(String message) {
        super(message);
    }
}