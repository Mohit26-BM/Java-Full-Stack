package com.company.boot.dto;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * The ONE error format returned for every failure, whatever the cause.
 *
 * {
 *   "timestamp": "...", "status": 404, "error": "Not Found",
 *   "message": "Account not found with id 99", "path": "/api/accounts/99",
 *   "errorId": "..."           (only for 500 - matches the server log entry)
 *   "validationErrors": {...}  (only for validation failures)
 * }
 * Never contains a stack trace, exception class name or SQL.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"timestamp", "status", "error", "message", "path", "errorId", "validationErrors"})
public class ErrorResponse {

    private final LocalDateTime timestamp = LocalDateTime.now().withNano(0);
    private final int status;
    private final String error;
    private final String message;
    private final String path;
    private String errorId;
    private Map<String, String> validationErrors;

    public ErrorResponse(int status, String error, String message, String path) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
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

    public String getPath() {
        return path;
    }

    public String getErrorId() {
        return errorId;
    }

    public void setErrorId(String errorId) {
        this.errorId = errorId;
    }

    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(Map<String, String> validationErrors) {
        this.validationErrors = validationErrors;
    }
}
