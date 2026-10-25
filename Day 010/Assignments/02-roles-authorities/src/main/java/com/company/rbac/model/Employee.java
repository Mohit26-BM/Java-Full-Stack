package com.company.rbac.model;

/** An employee record (kept in memory). managerId points to the employee's line manager. */
public record Employee(Long id, String name, String email, String department, String designation, Long managerId) {

    public Employee withDepartment(String newDepartment) {
        return new Employee(id, name, email, newDepartment, designation, managerId);
    }
}
