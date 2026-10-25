package com.example.hospital.event;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Extension: published by AppointmentService when an appointment is booked.
 *
 * It carries plain values, not entities: the listener runs AFTER the
 * transaction has committed, when lazy entity fields can no longer be loaded.
 */
public record AppointmentBookedEvent(String appointmentNumber,
                                     String patientName,
                                     String patientEmail,
                                     String patientPhone,
                                     String doctorName,
                                     LocalDate date,
                                     LocalTime startTime) {
}
