package com.example.employee.repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import com.example.employee.model.Employee;
import com.example.employee.model.EmployeeStatus;

/**
 * Stores employees IN MEMORY (Exercise 1 is about REST, not databases).
 * ConcurrentHashMap and AtomicLong are used because several HTTP requests
 * can arrive at the same time. The data is lost when the application stops.
 */
@Repository
public class EmployeeRepository {

    private final Map<Long, Employee> employees = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    /** Three sample employees so the API has data right after startup. */
    public EmployeeRepository() {
        save("Asha Rao", "asha.rao@company.com", "Engineering", new BigDecimal("95000.00"), EmployeeStatus.ACTIVE);
        save("Ravi Kumar", "ravi.kumar@company.com", "Finance", new BigDecimal("72000.00"), EmployeeStatus.ACTIVE);
        save("Meera Iyer", "meera.iyer@company.com", "HR", new BigDecimal("68000.00"), EmployeeStatus.ON_LEAVE);
    }

    public List<Employee> findAll() {
        List<Employee> result = new ArrayList<>(employees.values());
        result.sort((a, b) -> a.getId().compareTo(b.getId()));
        return result;
    }

    public Optional<Employee> findById(Long id) {
        return Optional.ofNullable(employees.get(id));
    }

    /** Creates a new employee with the next id. */
    public Employee save(String name, String email, String department, BigDecimal salary, EmployeeStatus status) {
        Long id = nextId.getAndIncrement();
        Employee employee = new Employee(id, name, email, department, salary, status);
        employees.put(id, employee);
        return employee;
    }

    public boolean deleteById(Long id) {
        return employees.remove(id) != null;
    }
}
