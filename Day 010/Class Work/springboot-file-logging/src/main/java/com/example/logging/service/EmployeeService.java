package com.example.logging.service;

import com.example.logging.model.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class EmployeeService {
    private static final Logger logger =
            LoggerFactory.getLogger(EmployeeService.class);

    private final Map<Integer, Employee> employees = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(100);

    public EmployeeService() {
        employees.put(1, new Employee(1, "Anita", "Engineering"));
        employees.put(2, new Employee(2, "Rahul", "Finance"));
        logger.info("EmployeeService initialized with {} sample employees",
                employees.size());
    }

    public Employee createEmployee(Employee employee) {
        logger.debug("Create employee request received for name={}",
                employee.getName());

        int id = idGenerator.incrementAndGet();
        employee.setId(id);
        employees.put(id, employee);

        logger.info("Employee created successfully: id={}, department={}",
                id, employee.getDepartment());
        return employee;
    }

    public Employee getEmployeeById(int id) {
        logger.debug("Searching for employee id={}", id);
        Employee employee = employees.get(id);

        if (employee == null) {
            logger.warn("Employee not found: id={}", id);
            throw new EmployeeNotFoundException(
                    "Employee with id " + id + " was not found");
        }

        logger.info("Employee retrieved successfully: id={}", id);
        return employee;
    }

    public Map<Integer, Employee> getAllEmployees() {
        logger.debug("Retrieving all employees; count={}", employees.size());
        return Map.copyOf(employees);
    }

    public void deleteEmployee(int id) {
        logger.debug("Delete employee request received: id={}", id);
        Employee removed = employees.remove(id);

        if (removed == null) {
            logger.warn("Cannot delete employee; id={} does not exist", id);
            throw new EmployeeNotFoundException(
                    "Employee with id " + id + " was not found");
        }

        logger.info("Employee deleted successfully: id={}", id);
    }
}
