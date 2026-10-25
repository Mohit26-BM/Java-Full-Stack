package com.company.secureapi.exception;

public enum ErrorCode {
    AUTH_REQUIRED,
    AUTH_INVALID_TOKEN,
    AUTH_BAD_CREDENTIALS,
    ACCESS_DENIED,

    VALIDATION_FAILED,
    EMPLOYEE_NOT_FOUND,
    USER_NOT_FOUND,
    DUPLICATE,
    MALFORMED_REQUEST
}
