package com.example.employee.dto;

import java.math.BigDecimal;

import com.example.employee.model.EmployeeStatus;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/** Body of POST /api/employees and PUT /api/employees/{id}. */
public record EmployeeRequest(

        @NotBlank(message = "name is required")
        @Size(max = 100, message = "name must be at most 100 characters")
        String name,

        @NotBlank(message = "email is required")
        @Email(message = "email must be a valid e-mail address")
        String email,

        @NotBlank(message = "department is required")
        String department,

        @NotNull(message = "salary is required")
        @Positive(message = "salary must be greater than 0")
        @Digits(integer = 10, fraction = 2, message = "salary may have at most 2 decimal places")
        BigDecimal salary,

        @NotNull(message = "status is required (ACTIVE, ON_LEAVE or INACTIVE)")
        EmployeeStatus status) {
}
