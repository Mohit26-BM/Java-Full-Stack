package com.company.boot.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown by the SERVICE when a request is well-formed (passed @Valid)
 * but breaks a business rule that needs data/state to decide.
 */
public class BusinessRuleException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final HttpStatus status;

    public BusinessRuleException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
