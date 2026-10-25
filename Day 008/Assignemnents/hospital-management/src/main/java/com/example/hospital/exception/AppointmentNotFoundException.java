package com.example.hospital.exception;

public class AppointmentNotFoundException extends NotFoundException {

    private static final long serialVersionUID = 1L;

    public AppointmentNotFoundException(Long id) {
        super("APPOINTMENT_NOT_FOUND", "Appointment not found with id " + id);
    }
}
