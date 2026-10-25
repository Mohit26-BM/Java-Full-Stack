package com.example.hospital.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Body of PUT /api/doctors/{id}/working-hours. Replaces the whole weekly
 * timetable; weekdays that are not listed are days off. An empty list removes
 * the timetable (the doctor can then be booked at any time again).
 */
public record WeeklyScheduleRequest(
        @NotNull(message = "days is required (use [] to remove the timetable)")
        @Size(max = 7, message = "days can have at most 7 entries")
        List<@Valid WorkingHoursRequest> days) {
}
