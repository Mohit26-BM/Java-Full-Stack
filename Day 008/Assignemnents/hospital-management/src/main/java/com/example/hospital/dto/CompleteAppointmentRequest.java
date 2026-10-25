package com.example.hospital.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Body of POST /api/appointments/{id}/complete
 * Completes the appointment AND creates its medical record in one transaction.
 */
public record CompleteAppointmentRequest(

        @NotNull(message = "version is required")
        Long version,

        @NotBlank(message = "diagnosis is required")
        @Size(max = 500, message = "diagnosis must be at most 500 characters")
        String diagnosis,

        @Size(max = 1000, message = "prescription must be at most 1000 characters")
        String prescription,

        @Size(max = 2000, message = "doctorNotes must be at most 2000 characters")
        String doctorNotes,

        LocalDate followUpDate) {
}
