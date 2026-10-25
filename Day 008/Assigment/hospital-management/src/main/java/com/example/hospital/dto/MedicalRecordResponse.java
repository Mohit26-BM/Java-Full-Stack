package com.example.hospital.dto;

import java.time.LocalDate;

public record MedicalRecordResponse(Long id,
                                    Long appointmentId,
                                    String appointmentNumber,
                                    String diagnosis,
                                    String prescription,
                                    String doctorNotes,
                                    LocalDate followUpDate) {
}
