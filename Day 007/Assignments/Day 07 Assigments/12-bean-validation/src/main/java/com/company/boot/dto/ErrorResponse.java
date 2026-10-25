package com.company.boot.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Uniform error body.
 * "errors" lists every violated constraint (field = "" for class-level rules).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"timestamp", "status", "error", "message", "errors"})
public class ErrorResponse {

    /** One violated constraint. */
    public record Violation(String field, Object rejectedValue, String message) {
    }

    private final LocalDateTime timestamp = LocalDateTime.now().withNano(0);
    private final int status;
    private final String error;
    private final String message;
    private final List<Violation> errors;

    public ErrorResponse(int status, String error, String message, List<Violation> errors) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.errors = errors;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public List<Violation> getErrors() {
        return errors;
    }
}
