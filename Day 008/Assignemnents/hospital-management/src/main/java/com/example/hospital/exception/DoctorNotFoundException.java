package com.example.hospital.exception;

public class DoctorNotFoundException extends NotFoundException {

    private static final long serialVersionUID = 1L;

    public DoctorNotFoundException(Long id) {
        super("DOCTOR_NOT_FOUND", "Doctor not found with id " + id);
    }
}
