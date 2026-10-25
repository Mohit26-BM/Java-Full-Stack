package com.example.hospital.exception;

public class InvalidMedicalRecordException extends BusinessRuleException {

    private static final long serialVersionUID = 1L;

    public InvalidMedicalRecordException(String message) {
        super("INVALID_MEDICAL_RECORD", message);
    }
}
