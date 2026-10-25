package com.example.hospital.exception;

/**
 * Base class of all application exceptions.
 * errorCode is a short, stable code for API clients, e.g. "PATIENT_NOT_FOUND".
 *
 * The exceptions do not contain HTTP status codes: the service layer does not
 * know about HTTP. GlobalExceptionHandler maps each category to a status:
 *   NotFoundException      -> 404
 *   ConflictException      -> 409
 *   BusinessRuleException  -> 422
 *   BadRequestException    -> 400
 */
public abstract class HospitalException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String errorCode;

    protected HospitalException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
