package com.example.hospital.dto;

import java.time.LocalDateTime;

import com.example.hospital.entity.AppointmentStatus;

/** One row of GET /api/appointments/{id}/status-history (fromStatus is null for the booking). */
public record StatusChangeResponse(AppointmentStatus fromStatus,
                                   AppointmentStatus toStatus,
                                   LocalDateTime changedAt,
                                   String changedBy,
                                   String note) {
}
