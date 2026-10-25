package com.example.hospital.exception;

/** Category: the request is well-formed but a business rule forbids it (-> 422). */
public abstract class BusinessRuleException extends HospitalException {

    private static final long serialVersionUID = 1L;

    protected BusinessRuleException(String errorCode, String message) {
        super(errorCode, message);
    }
}
