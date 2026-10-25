package com.example.hospital.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/** Body of POST /api/doctors (register a doctor). */
public record DoctorRequest(

        @NotBlank(message = "doctorCode is required")
        @Size(max = 10, message = "doctorCode must be at most 10 characters")
        String doctorCode,

        @NotBlank(message = "name is required")
        @Size(max = 100, message = "name must be at most 100 characters")
        String name,

        @NotBlank(message = "specialization is required")
        @Size(max = 100, message = "specialization must be at most 100 characters")
        String specialization,

        @NotBlank(message = "email is required")
        @Email(message = "email must be a valid e-mail address")
        String email,

        @NotBlank(message = "phone is required")
        @Pattern(regexp = "\\d{10}", message = "phone must be exactly 10 digits")
        String phone,

        @NotNull(message = "consultationFee is required")
        @Positive(message = "consultationFee must be greater than 0")
        @Digits(integer = 8, fraction = 2, message = "consultationFee may have at most 2 decimal places")
        BigDecimal consultationFee,

        @NotNull(message = "departmentId is required")
        Long departmentId) {
}
