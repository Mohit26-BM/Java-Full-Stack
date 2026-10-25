package com.example.hospital.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Body of POST /api/doctors/{id}/leaves. Both dates are days off (inclusive). */
public record LeaveRequest(
        @NotNull(message = "startDate is required")
        @FutureOrPresent(message = "startDate must not be in the past")
        LocalDate startDate,

        @NotNull(message = "endDate is required")
        LocalDate endDate,

        @Size(max = 255, message = "reason must be at most 255 characters")
        String reason) {

    @AssertTrue(message = "endDate must be on or after startDate")
    public boolean isEndDateOnOrAfterStartDate() {
        if (startDate == null || endDate == null) {
            return true;
        }
        return !endDate.isBefore(startDate);
    }
}
