package com.example.employee.dto;

import com.example.employee.model.EmployeeStatus;

import jakarta.validation.constraints.NotNull;

/** Body of PATCH /api/employees/{id}/status - only the status changes. */
public record StatusUpdateRequest(

        @NotNull(message = "status is required (ACTIVE, ON_LEAVE or INACTIVE)")
        EmployeeStatus status) {
}
