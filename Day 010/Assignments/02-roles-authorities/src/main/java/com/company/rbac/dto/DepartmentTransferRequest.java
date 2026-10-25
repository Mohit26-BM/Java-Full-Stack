package com.company.rbac.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DepartmentTransferRequest(@NotBlank @Size(max = 50) String department) {
}
