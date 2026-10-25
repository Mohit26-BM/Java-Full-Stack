package com.example.employee.dto;

import java.math.BigDecimal;

import com.example.employee.model.Employee;
import com.example.employee.model.EmployeeStatus;

/** What the API returns for an employee. */
public record EmployeeResponse(Long id,
                               String name,
                               String email,
                               String department,
                               BigDecimal salary,
                               EmployeeStatus status) {

    public static EmployeeResponse from(Employee employee) {
        return new EmployeeResponse(employee.getId(), employee.getName(), employee.getEmail(),
                employee.getDepartment(), employee.getSalary(), employee.getStatus());
    }
}
