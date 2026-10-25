package com.company.security.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.security.model.Employee;
import com.company.security.service.EmployeeService;

/**
 * Employee data: requires authentication (/api/employees/**), except the
 * public head count, which shows why matcher ORDER matters (see SecurityConfig).
 */
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public List<Employee> findAll() {
        return employeeService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> findById(@PathVariable("id") Long id) {
        return ResponseEntity.of(employeeService.findById(id));
    }

    /** Public: matched by a permitAll rule placed BEFORE the general /api/employees/** rule. */
    @GetMapping("/count")
    public Map<String, Integer> count() {
        return Map.of("employees", employeeService.count());
    }
}
