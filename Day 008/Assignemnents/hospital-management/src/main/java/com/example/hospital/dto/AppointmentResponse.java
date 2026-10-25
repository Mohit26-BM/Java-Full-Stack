package com.example.hospital.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.example.hospital.entity.AppointmentStatus;

/**
 * An appointment as JSON. The patient and doctor are described by a few
 * fields instead of nested entities. "version" must be sent back when the
 * appointment is changed (optimistic locking).
 */
public record AppointmentResponse(Long id,
                                  String appointmentNumber,
                                  Long patientId,
                                  String patientNumber,
                                  String patientName,
                                  Long doctorId,
                                  String doctorCode,
                                  String doctorName,
                                  String departmentName,
                                  LocalDate appointmentDate,
                                  LocalTime startTime,
                                  LocalTime endTime,
                                  AppointmentStatus status,
                                  String reason,
                                  BigDecimal fee,
                                  Long version,
                                  LocalDateTime createdAt,
                                  LocalDateTime updatedAt) {
}
