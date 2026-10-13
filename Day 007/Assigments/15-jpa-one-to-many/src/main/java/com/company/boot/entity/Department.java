package com.company.boot.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

/**
 * The "ONE" side: one department has many employees.
 *
 * This is the INVERSE (non-owning) side of the relationship:
 * mappedBy = "department" says "the foreign key is managed by the
 * Employee.department field" - this class has NO column for it.
 */
@Entity
@Table(name = "a15_departments")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "department_id")
    private Long id;

    @Column(name = "name", nullable = false, length = 80)
    private String name;

    @Column(name = "location", length = 80)
    private String location;

    /**
     * mappedBy      - inverse side; points at the owning field Employee.department
     * cascade = ALL - persist/merge/remove of a Department is passed on to its employees
     * orphanRemoval - an employee removed from this list is DELETED from the table
     * fetch = LAZY  - employees are loaded only when the list is first accessed
     *                 (LAZY is already the default for @OneToMany; written for clarity)
     */
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("id ASC")
    private List<Employee> employees = new ArrayList<>();

    protected Department() {
    }

    public Department(String name, String location) {
        this.name = name;
        this.location = location;
    }

    /**
     * Keeps BOTH sides in sync. Only Employee.department decides the foreign key,
     * so setting it is essential; adding to the list keeps the in-memory object correct.
     */
    public void addEmployee(Employee employee) {
        employees.add(employee);
        employee.setDepartment(this);
    }

    public void removeEmployee(Employee employee) {
        employees.remove(employee);
        employee.setDepartment(null);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public List<Employee> getEmployees() {
        return employees;
    }
}
