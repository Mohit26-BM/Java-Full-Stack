package com.company.boot.dto;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/** Uniform error body returned by GlobalExceptionHandler. */
@JsonInclude(JsonInclude.Include.NON_NULL)      // omit fieldErrors when there are none
@JsonPropertyOrder({"timestamp", "status", "error", "message", "fieldErrors"})
public class ErrorResponse {

    private final LocalDateTime timestamp = LocalDateTime.now().withNano(0);
    private final int status;
    private final String error;
    private final String message;
    private final Map<String, String> fieldErrors;

    public ErrorResponse(int status, String error, String message, Map<String, String> fieldErrors) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.fieldErrors = fieldErrors;
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

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }
}
