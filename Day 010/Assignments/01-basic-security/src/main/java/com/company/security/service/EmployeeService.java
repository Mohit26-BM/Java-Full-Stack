package com.company.security.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.company.security.model.Employee;

/** Read-only employee data for the portal. */
@Service
public class EmployeeService {

    private final List<Employee> employees = List.of(
            new Employee(1L, "Asha Verma", "Engineering", "Software Engineer"),
            new Employee(2L, "Ravi Iyer", "Engineering", "Team Lead"),
            new Employee(3L, "Meena Das", "Human Resources", "HR Executive"),
            new Employee(4L, "Karan Shah", "Finance", "Accountant"));

    public List<Employee> findAll() {
        return employees;
    }

    public Optional<Employee> findById(Long id) {
        return employees.stream().filter(e -> e.id().equals(id)).findFirst();
    }

    public int count() {
        return employees.size();
    }
}
