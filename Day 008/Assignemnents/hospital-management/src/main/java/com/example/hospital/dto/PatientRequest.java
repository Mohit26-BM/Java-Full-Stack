package com.example.hospital.dto;

import java.time.LocalDate;

import com.example.hospital.entity.Gender;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Body of POST /api/patients (register a patient). */
public record PatientRequest(

        @NotBlank(message = "patientNumber is required")
        @Size(max = 10, message = "patientNumber must be at most 10 characters")
        String patientNumber,

        @NotBlank(message = "name is required")
        @Size(max = 100, message = "name must be at most 100 characters")
        String name,

        @NotNull(message = "dateOfBirth is required")
        @Past(message = "dateOfBirth must be in the past")
        LocalDate dateOfBirth,

        @NotNull(message = "gender is required (MALE, FEMALE or OTHER)")
        Gender gender,

        @Email(message = "email must be a valid e-mail address")
        @Size(max = 150, message = "email must be at most 150 characters")
        String email,

        @NotBlank(message = "phone is required")
        @Pattern(regexp = "\\d{10}", message = "phone must be exactly 10 digits")
        String phone,

        @Size(max = 255, message = "address must be at most 255 characters")
        String address) {
}
