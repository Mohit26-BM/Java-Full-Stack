package com.company.secureapi.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.company.secureapi.entity.Employee;
import com.company.secureapi.entity.SalaryAudit;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public final class Dtos {
    private Dtos() {
    }

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {
        @Override
        public String toString() {
            return "LoginRequest[" + username + ", ***]";
        }
    }

    public record TokenResponse(String tokenType, String accessToken, long expiresIn) {
    }

    public record CreateEmployeeRequest(
            @NotBlank @Size(max = 80) String name,
            @NotBlank @Email @Size(max = 120) String email,
            @NotBlank @Size(max = 50) String department,
            @NotBlank @Size(max = 50) String designation,
            @NotNull @Positive @Digits(integer = 9, fraction = 2) BigDecimal salary,
            @Positive Long managerId) {
    }

    public record UpdateEmployeeRequest(
            @NotBlank @Size(max = 80) String name,
            @NotBlank @Email @Size(max = 120) String email,
            @NotBlank @Size(max = 50) String department,
            @NotBlank @Size(max = 50) String designation,
            @Positive Long managerId,
            @NotNull Employee.Status status) {
    }

    public record SalaryUpdateRequest(@NotNull @Positive @Digits(integer = 9, fraction = 2) BigDecimal salary) {
    }

    public record EmployeeSummary(Long id, String name, String department, String designation) {
        public static EmployeeSummary of(Employee e) {
            return new EmployeeSummary(e.getId(), e.getName(), e.getDepartment(), e.getDesignation());
        }
    }

    public record EmployeeDetail(Long id, String name, String email, String department, String designation,
                                 Long managerId, Employee.Status status) {
        public static EmployeeDetail of(Employee e) {
            return new EmployeeDetail(e.getId(), e.getName(), e.getEmail(), e.getDepartment(), e.getDesignation(),
                    e.getManagerId(), e.getStatus());
        }
    }

    public record SalaryResponse(Long employeeId, BigDecimal salary) {
    }

    public record SalaryChange(BigDecimal oldSalary, BigDecimal newSalary, String changedBy, LocalDateTime changedAt) {
        public static SalaryChange of(SalaryAudit a) {
            return new SalaryChange(a.getOldSalary(), a.getNewSalary(), a.getChangedBy(), a.getChangedAt());
        }
    }

    public record CreateUserRequest(
            @NotBlank @Pattern(regexp = "^[a-z0-9._-]{3,30}$", message = "must be 3-30 lower-case letters, digits, . _ -")
            String username,
            @NotNull @Size(min = 8, max = 72) String password,
            @NotNull @Positive Long employeeId,
            @NotNull @Pattern(regexp = "^(ADMIN|HR|MANAGER|EMPLOYEE)$", message = "must be ADMIN, HR, MANAGER or EMPLOYEE")
            String role) {
        @Override
        public String toString() {
            return "CreateUserRequest[" + username + ", ***]";
        }
    }

    public record UserResponse(Long id, String username, boolean enabled, Long employeeId, List<String> roles) {
    }
}
