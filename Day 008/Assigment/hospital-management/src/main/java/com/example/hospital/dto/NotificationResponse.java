package com.example.hospital.dto;

import java.time.LocalDateTime;

/** A notification the system "sent" (written to the log and kept in memory for the demo). */
public record NotificationResponse(LocalDateTime sentAt,
                                   String type,
                                   String appointmentNumber,
                                   String recipient,
                                   String message) {
}
