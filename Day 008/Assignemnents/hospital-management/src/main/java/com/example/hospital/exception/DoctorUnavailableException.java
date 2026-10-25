package com.example.hospital.exception;

/** The doctor cannot take the appointment: inactive, or the time slot overlaps another appointment. */
public class DoctorUnavailableException extends ConflictException {

    private static final long serialVersionUID = 1L;

    public DoctorUnavailableException(String message) {
        super("DOCTOR_UNAVAILABLE", message);
    }
}
