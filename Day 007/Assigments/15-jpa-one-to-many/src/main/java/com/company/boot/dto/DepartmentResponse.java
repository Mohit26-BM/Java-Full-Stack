package com.company.boot.dto;

import java.util.List;

import com.company.boot.entity.Department;

/**
 * Department WITH its employees. Each employee is an EmployeeSummary, which
 * has no department field at all -> the object graph ends here (no cycle).
 */
public record DepartmentResponse(Long id,
                                 String name,
                                 String location,
                                 int employeeCount,
                                 List<EmployeeSummary> employees) {

    /** An employee inside a department - the department is already the parent. */
    public record EmployeeSummary(Long id, String name, String email, java.math.BigDecimal salary) {
    }

    public static DepartmentResponse from(Department department) {
        List<EmployeeSummary> employees = department.getEmployees().stream()
                .map(e -> new EmployeeSummary(e.getId(), e.getName(), e.getEmail(), e.getSalary()))
                .toList();
        return new DepartmentResponse(department.getId(), department.getName(), department.getLocation(),
                employees.size(), employees);
    }
}
