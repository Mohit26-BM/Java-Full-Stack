package com.company.boot.exception;

/**
 * A CHECKED exception (extends Exception, not RuntimeException).
 * By default Spring does NOT roll back a transaction for checked exceptions.
 */
public class TransferException extends Exception {

    private static final long serialVersionUID = 1L;

    public TransferException(String message) {
        super(message);
    }
}
