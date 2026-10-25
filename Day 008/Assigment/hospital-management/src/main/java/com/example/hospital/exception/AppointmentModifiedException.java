package com.example.hospital.exception;

/**
 * Optimistic locking: the client sent an old version number, which means
 * someone else changed the appointment after this client loaded it.
 */
public class AppointmentModifiedException extends ConflictException {

    private static final long serialVersionUID = 1L;

    public AppointmentModifiedException(String appointmentNumber, Long clientVersion, Long currentVersion) {
        super("APPOINTMENT_MODIFIED",
                "Appointment " + appointmentNumber + " was changed by another user (you sent version "
                + clientVersion + ", current version is " + currentVersion + "). Reload it and try again.");
    }
}
