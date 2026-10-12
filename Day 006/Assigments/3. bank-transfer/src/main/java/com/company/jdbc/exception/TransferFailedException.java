package com.company.jdbc.exception;

/*
Thrown when a transfer failed for a technical reason
(SQL error, simulated failure ...) and was ROLLED BACK.
No balance was changed and no history row was saved.
*/
public class TransferFailedException extends Exception {

    private static final long serialVersionUID = 1L;

    public TransferFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}