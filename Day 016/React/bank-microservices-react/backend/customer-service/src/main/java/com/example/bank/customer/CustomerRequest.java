package com.example.bank.customer;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CustomerRequest(
        @NotBlank(message = "Name is required") @Size(max = 100, message = "Name must be at most 100 characters") String name,
        @NotBlank(message = "Email is required") @Email(message = "Email is not valid") @Size(max = 255) String email,
        @NotNull(message = "Date of birth is required") @Past(message = "Date of birth must be in the past") LocalDate dateOfBirth,
        @NotBlank(message = "Gender is required") @Pattern(regexp = "Female|Male|Other", message = "Gender must be Female, Male or Other") String gender,
        @NotBlank(message = "City is required") @Size(max = 80, message = "City must be at most 80 characters") String city) {
}
