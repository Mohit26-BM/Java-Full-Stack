package com.example.hospital.dto;

import java.time.LocalTime;

/**
 * One generated slot [start, end).
 * status: FREE (can be booked), BOOKED (overlaps an appointment) or PAST (already started).
 */
public record TimeSlot(LocalTime start,
                       LocalTime end,
                       boolean available,
                       String status) {
}
