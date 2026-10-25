package com.example.hospital.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Body of POST /api/appointments (book an appointment). */
public record CreateAppointmentRequest(

        @NotNull(message = "patientId is required")
        Long patientId,

        @NotNull(message = "doctorId is required")
        Long doctorId,

        @NotNull(message = "appointmentDate is required")
        @FutureOrPresent(message = "appointmentDate must not be in the past")
        LocalDate appointmentDate,

        @NotNull(message = "startTime is required")
        LocalTime startTime,

        @NotNull(message = "endTime is required")
        LocalTime endTime,

        @Size(max = 500, message = "reason must be at most 500 characters")
        String reason) {

    /**
     * Checks two fields together. Bean Validation calls this "getter" and reports
     * the property name "endTimeAfterStartTime" when it returns false.
     * (If a time is missing, @NotNull already reports it.)
     */
    @AssertTrue(message = "endTime must be after startTime")
    public boolean isEndTimeAfterStartTime() {
        if (startTime == null || endTime == null) {
            return true;
        }
        return endTime.isAfter(startTime);
    }
}
