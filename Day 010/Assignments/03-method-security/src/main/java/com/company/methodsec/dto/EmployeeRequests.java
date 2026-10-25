package com.company.methodsec.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public final class EmployeeRequests {
    private EmployeeRequests() {
    }

    public record SalaryUpdate(@NotNull @Positive @Digits(integer = 9, fraction = 2) BigDecimal salary) {
    }

    public record DesignationUpdate(@NotBlank @Size(max = 50) String designation) {
    }
}
