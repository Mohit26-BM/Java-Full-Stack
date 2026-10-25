package com.example.hospital.exception;

/** A doctor with the same doctor code or e-mail already exists. */
public class DuplicateDoctorException extends ConflictException {

    private static final long serialVersionUID = 1L;

    public DuplicateDoctorException(String message) {
        super("DUPLICATE_DOCTOR", message);
    }
}
