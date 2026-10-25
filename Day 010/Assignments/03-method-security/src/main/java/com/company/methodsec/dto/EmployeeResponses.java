package com.company.methodsec.dto;

import java.math.BigDecimal;

import com.company.methodsec.model.Employee;

/** What the API returns. Salary appears ONLY in SalaryResponse, which has its own rule. */
public final class EmployeeResponses {

    private EmployeeResponses() {
    }

    public record EmployeeSummary(Long id, String name, String department) {
        public static EmployeeSummary of(Employee e) {
            return new EmployeeSummary(e.id(), e.name(), e.department());
        }
    }

    public record EmployeeDetail(Long id, String name, String department, String designation, Long managerId) {
        public static EmployeeDetail of(Employee e) {
            return new EmployeeDetail(e.id(), e.name(), e.department(), e.designation(), e.managerId());
        }
    }

    public record SalaryResponse(Long employeeId, BigDecimal salary) {
    }
}
