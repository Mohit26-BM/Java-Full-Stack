package com.example.hospital.exception;

/** The appointment date is in the past, or endTime is not after startTime. */
public class InvalidAppointmentTimeException extends BadRequestException {

    private static final long serialVersionUID = 1L;

    public InvalidAppointmentTimeException(String message) {
        super("INVALID_APPOINTMENT_TIME", message);
    }
}
