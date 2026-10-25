package com.company.methodsec.model;

import java.math.BigDecimal;

/**
 * An employee record. username links the record to a login (null for staff
 * without one); managerId points to the line manager. salary is the sensitive
 * field that the method rules protect.
 */
public record Employee(Long id, String name, String department, String designation,
                       Long managerId, BigDecimal salary, String username) {

    public Employee withSalary(BigDecimal newSalary) {
        return new Employee(id, name, department, designation, managerId, newSalary, username);
    }

    public Employee withDesignation(String newDesignation) {
        return new Employee(id, name, department, newDesignation, managerId, salary, username);
    }
}
