package com.company.methodsec.repository;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.company.methodsec.model.Employee;

@Repository
public class EmployeeRepository {
    private final Map<Long, Employee> employees = new ConcurrentHashMap<>();

    public EmployeeRepository() {
        resetToSampleData();
    }

    public final void resetToSampleData() {
        employees.clear();
        List.of(
                new Employee(1L, "Aditi Rao", "Management", "Director", null, new BigDecimal("250000"), "admin"),
                new Employee(2L, "Hana Kapoor", "Human Resources", "HR Manager", 1L, new BigDecimal("140000"), "hana"),
                new Employee(3L, "Manoj Pillai", "Engineering", "Engineering Manager", 1L, new BigDecimal("160000"), "manoj"),
                new Employee(4L, "Asha Verma", "Engineering", "Software Engineer", 3L, new BigDecimal("95000"), "asha"),
                new Employee(5L, "Ravi Iyer", "Engineering", "Software Engineer", 3L, new BigDecimal("92000"), "ravi"),
                new Employee(6L, "Meena Das", "Human Resources", "HR Executive", 2L, new BigDecimal("70000"), null),
                new Employee(7L, "Karan Shah", "Finance", "Accountant", 1L, new BigDecimal("80000"), null))
            .forEach(e -> employees.put(e.id(), e));
    }

    public List<Employee> findAll() {
        return employees.values().stream().sorted(Comparator.comparing(Employee::id)).toList();
    }

    public Optional<Employee> findById(Long id) {
        return Optional.ofNullable(employees.get(id));
    }

    public Optional<Employee> findByUsername(String username) {
        return employees.values().stream().filter(e -> username.equals(e.username())).findFirst();
    }

    public List<Employee> findByManagerId(Long managerId) {
        return findAll().stream().filter(e -> managerId.equals(e.managerId())).toList();
    }

    public void save(Employee employee) {
        employees.put(employee.id(), employee);
    }

    public boolean delete(Long id) {
        return employees.remove(id) != null;
    }
}
