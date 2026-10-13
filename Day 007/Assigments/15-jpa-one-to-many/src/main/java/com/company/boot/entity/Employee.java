package com.company.boot.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * The "MANY" side: many employees belong to one department.
 *
 * This is the OWNING side of the relationship: its table (a15_employees)
 * holds the foreign key column department_id, and only changes to THIS
 * field are written to that column.
 */
@Entity
@Table(name = "a15_employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "salary", nullable = false, precision = 10, scale = 2)
    private BigDecimal salary;

    /**
     * @ManyToOne    - many employees -> one department
     * @JoinColumn   - the foreign key column in a15_employees
     * fetch = LAZY  - the default for @ManyToOne is EAGER; LAZY avoids loading the
     *                 department every time an employee is loaded
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    protected Employee() {
    }

    public Employee(String name, String email, BigDecimal salary) {
        this.name = name;
        this.email = email;
        this.salary = salary;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public Department getDepartment() {
        return department;
    }

    void setDepartment(Department department) {     // package-private: use Department.addEmployee()
        this.department = department;
    }
}
