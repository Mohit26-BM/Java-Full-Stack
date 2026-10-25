package com.example.hospital.dto;

import com.example.hospital.entity.AppointmentStatus;

import jakarta.validation.constraints.NotNull;

/**
 * Body of PATCH /api/appointments/{id}/status
 *
 * version = the version the client saw when it loaded the appointment.
 * If someone else changed the appointment in the meantime, the versions
 * differ and the change is rejected (409) instead of overwriting theirs.
 */
public record UpdateStatusRequest(

        @NotNull(message = "status is required")
        AppointmentStatus status,

        @NotNull(message = "version is required")
        Long version) {
}
