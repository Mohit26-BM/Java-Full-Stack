package com.example.hospital.entity;

/**
 * Appointment lifecycle:
 *
 *   BOOKED ----> CONFIRMED ----> COMPLETED
 *     |              |
 *     +--> CANCELLED <--+
 *     +--> NO_SHOW   <--+
 *
 * COMPLETED, CANCELLED and NO_SHOW are final.
 * The allowed changes are checked in AppointmentService.
 */
public enum AppointmentStatus {
    BOOKED,
    CONFIRMED,
    COMPLETED,
    CANCELLED,
    NO_SHOW
}
