package com.company.rbac.model;

public record Employee(Long id, String name, String email, String department, String designation, Long managerId) {
    public Employee withDepartment(String newDepartment) {
        return new Employee(id, name, email, newDepartment, designation, managerId);
    }
}
