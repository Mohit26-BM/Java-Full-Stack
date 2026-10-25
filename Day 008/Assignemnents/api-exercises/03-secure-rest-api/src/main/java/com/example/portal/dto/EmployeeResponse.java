package com.example.portal.dto;

import com.example.portal.model.Employee;
import com.example.portal.model.EmployeeStatus;

/**
 * Employee details visible to EVERY role.
 * The salary is deliberately NOT included - see PayrollResponse (HR / ADMIN only).
 */
public record EmployeeResponse(Long id,
                               String name,
                               String email,
                               String department,
                               String designation,
                               EmployeeStatus status) {

    public static EmployeeResponse from(Employee employee) {
        return new EmployeeResponse(employee.getId(), employee.getName(), employee.getEmail(),
                employee.getDepartment(), employee.getDesignation(), employee.getStatus());
    }
}
