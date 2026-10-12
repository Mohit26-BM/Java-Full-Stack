package com.company.jdbc.exception;

/*
Thrown when a transfer request is invalid before touching the database:
amount is zero / negative / has more than 2 decimals, or the source
and destination accounts are the same.
*/
public class InvalidTransferException extends Exception {

    private static final long serialVersionUID = 1L;

    public InvalidTransferException(String message) {
        super(message);
    }
}