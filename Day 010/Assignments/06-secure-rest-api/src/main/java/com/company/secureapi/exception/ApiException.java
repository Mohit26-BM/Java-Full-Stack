package com.company.secureapi.exception;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {
    private final HttpStatus status;
    private final ErrorCode code;

    public ApiException(HttpStatus status, ErrorCode code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public HttpStatus getStatus() { return status; }
    public ErrorCode getCode() { return code; }

    public static ApiException employeeNotFound(Long id) {
        return new ApiException(HttpStatus.NOT_FOUND, ErrorCode.EMPLOYEE_NOT_FOUND, "Employee " + id + " not found");
    }

    public static ApiException duplicate(String message) {
        return new ApiException(HttpStatus.CONFLICT, ErrorCode.DUPLICATE, message);
    }
}
