package com.example.hospital.exception;

public class DuplicatePatientException extends ConflictException {

    private static final long serialVersionUID = 1L;

    public DuplicatePatientException(String patientNumber) {
        super("DUPLICATE_PATIENT", "A patient with number " + patientNumber + " already exists");
    }
}
