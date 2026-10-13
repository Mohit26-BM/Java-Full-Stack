package com.company.boot.dto;

import java.math.BigDecimal;

import com.company.boot.entity.Employee;

/**
 * Employee as JSON. It refers to its department by id + name only - NOT by a
 * Department object - so the JSON cannot loop back (no recursion possible).
 */
public record EmployeeResponse(Long id,
                               String name,
                               String email,
                               BigDecimal salary,
                               Long departmentId,
                               String departmentName) {

    public static EmployeeResponse from(Employee employee) {
        return new EmployeeResponse(employee.getId(), employee.getName(), employee.getEmail(),
                employee.getSalary(), employee.getDepartment().getId(), employee.getDepartment().getName());
    }
}
