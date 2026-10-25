package com.example.hospital.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * One completed visit in a patient's medical history.
 * Filled directly by a JPQL "SELECT new ..." query (AppointmentRepository.findPatientHistory).
 * The medical-record fields are null if no record has been written yet.
 */
public record PatientHistoryItem(Long appointmentId,
                                 String appointmentNumber,
                                 LocalDate appointmentDate,
                                 LocalTime startTime,
                                 String doctorName,
                                 String specialization,
                                 BigDecimal fee,
                                 Long medicalRecordId,
                                 String diagnosis,
                                 String prescription,
                                 LocalDate followUpDate) {
}
