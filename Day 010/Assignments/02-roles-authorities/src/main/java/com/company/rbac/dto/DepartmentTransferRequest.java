package com.company.rbac.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Body of PATCH /api/employees/{id}/department. */
public record DepartmentTransferRequest(@NotBlank @Size(max = 50) String department) {
}
