package com.example.hospital.event;

import java.time.LocalDate;
import java.time.LocalTime;

import com.example.hospital.entity.AppointmentStatus;

/** Extension: published whenever an appointment's status changes (plain values, see AppointmentBookedEvent). */
public record AppointmentStatusChangedEvent(String appointmentNumber,
                                            AppointmentStatus fromStatus,
                                            AppointmentStatus toStatus,
                                            String changedBy,
                                            String patientName,
                                            String patientEmail,
                                            String patientPhone,
                                            String doctorName,
                                            LocalDate date,
                                            LocalTime startTime) {
}
