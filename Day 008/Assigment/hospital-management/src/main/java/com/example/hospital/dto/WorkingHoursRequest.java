package com.example.hospital.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** One weekday of a doctor's timetable (part of PUT /api/doctors/{id}/working-hours). */
public record WorkingHoursRequest(
        @NotNull(message = "dayOfWeek is required (MONDAY ... SUNDAY)")
        DayOfWeek dayOfWeek,

        @NotNull(message = "startTime is required")
        LocalTime startTime,

        @NotNull(message = "endTime is required")
        LocalTime endTime,

        @NotNull(message = "slotMinutes is required")
        @Min(value = 5, message = "slotMinutes must be at least 5")
        @Max(value = 240, message = "slotMinutes must be at most 240")
        Integer slotMinutes) {

    @AssertTrue(message = "endTime must be after startTime")
    public boolean isEndTimeAfterStartTime() {
        if (startTime == null || endTime == null) {
            return true;
        }
        return endTime.isAfter(startTime);
    }
}
