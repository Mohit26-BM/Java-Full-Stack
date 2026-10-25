package com.example.hospital.dto;

import java.time.LocalDate;

import com.example.hospital.entity.Gender;

public record PatientResponse(Long id,
                              String patientNumber,
                              String name,
                              LocalDate dateOfBirth,
                              Gender gender,
                              String email,
                              String phone,
                              String address,
                              Boolean active) {
}
