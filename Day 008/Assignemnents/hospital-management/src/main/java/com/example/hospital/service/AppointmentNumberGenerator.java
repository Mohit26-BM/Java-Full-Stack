package com.example.hospital.service;

import java.util.Locale;
import java.util.UUID;

import org.springframework.stereotype.Component;

/**
 * Generates appointment numbers such as "APT-3F9A1C2B".
 *
 * The number is created BEFORE the INSERT, so the appointment is saved with
 * a single statement. The UNIQUE constraint on appointments.appointment_number
 * guarantees uniqueness in the database (a random 8-hex-digit value repeats
 * roughly once in 4 billion; the constraint would reject that rare case).
 *
 * It is a separate bean so that unit tests can replace it with a fixed value.
 */
@Component
public class AppointmentNumberGenerator {

    public String nextNumber() {
        String random = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return "APT-" + random.toUpperCase(Locale.ROOT);
    }
}
