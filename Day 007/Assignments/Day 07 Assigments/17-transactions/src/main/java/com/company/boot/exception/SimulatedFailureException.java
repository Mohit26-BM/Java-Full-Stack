package com.company.boot.exception;

/**
 * An UNCHECKED exception (RuntimeException) thrown on purpose between the
 * debit and the credit. Spring rolls back on unchecked exceptions by default.
 */
public class SimulatedFailureException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SimulatedFailureException(String message) {
        super(message);
    }
}
