package com.example.hospital.exception;

public class PatientNotFoundException extends NotFoundException {

    private static final long serialVersionUID = 1L;

    public PatientNotFoundException(Long id) {
        super("PATIENT_NOT_FOUND", "Patient not found with id " + id);
    }
}
