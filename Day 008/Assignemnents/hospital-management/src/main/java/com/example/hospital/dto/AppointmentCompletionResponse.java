package com.example.hospital.dto;

/** Result of POST /api/appointments/{id}/complete: both changes made in one transaction. */
public record AppointmentCompletionResponse(AppointmentResponse appointment, MedicalRecordResponse medicalRecord) {
}
