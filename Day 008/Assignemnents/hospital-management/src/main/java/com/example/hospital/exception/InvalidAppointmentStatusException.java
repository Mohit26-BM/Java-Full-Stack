package com.example.hospital.exception;

import com.example.hospital.entity.AppointmentStatus;

public class InvalidAppointmentStatusException extends BusinessRuleException {

    private static final long serialVersionUID = 1L;

    public InvalidAppointmentStatusException(String appointmentNumber, AppointmentStatus from, AppointmentStatus to) {
        super("INVALID_APPOINTMENT_STATUS",
                "Appointment " + appointmentNumber + " cannot change from " + from + " to " + to);
    }

    public InvalidAppointmentStatusException(String message) {
        super("INVALID_APPOINTMENT_STATUS", message);
    }
}
