package com.example.hospital.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record WorkingHoursResponse(DayOfWeek dayOfWeek,
                                   LocalTime startTime,
                                   LocalTime endTime,
                                   Integer slotMinutes) {
}
