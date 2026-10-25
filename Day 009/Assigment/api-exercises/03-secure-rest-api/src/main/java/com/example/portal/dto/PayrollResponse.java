package com.example.portal.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.example.portal.model.Employee;

/** Payroll information of one employee - only HR and ADMIN may read it. */
public record PayrollResponse(Long employeeId,
                              String name,
                              String department,
                              BigDecimal annualSalary,
                              BigDecimal monthlySalary) {

    public static PayrollResponse from(Employee employee) {
        BigDecimal monthly = employee.getSalary().divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
        return new PayrollResponse(employee.getId(), employee.getName(), employee.getDepartment(),
                employee.getSalary(), monthly);
    }
}
