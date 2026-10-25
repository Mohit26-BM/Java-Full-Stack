package com.example.hospital.dto;

/** Report row: a doctor and the number of appointments in a date range (JPQL "SELECT new ..."). */
public record DoctorWorkload(Long doctorId, String doctorCode, String doctorName, Long appointmentCount) {
}
