package com.company.rbac.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.rbac.dto.DepartmentTransferRequest;
import com.company.rbac.dto.EmployeeRequest;
import com.company.rbac.model.Employee;
import com.company.rbac.security.PortalUserDetailsService;
import com.company.rbac.service.EmployeeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    private final EmployeeService employeeService;
    private final PortalUserDetailsService users;

    public EmployeeController(EmployeeService employeeService, PortalUserDetailsService users) {
        this.employeeService = employeeService;
        this.users = users;
    }

    @GetMapping
    public List<Employee> findAll() {
        return employeeService.findAll();
    }

    @GetMapping("/team")
    public List<Employee> myTeam(Authentication authentication) {
        return users.employeeIdOf(authentication.getName())
                .map(employeeService::findTeam)
                .orElse(List.of());
    }

    @GetMapping("/{id}")
    public Employee findById(@PathVariable("id") Long id) {
        return employeeService.findById(id);
    }

    @PostMapping
    public ResponseEntity<Employee> create(@Valid @RequestBody EmployeeRequest request) {
        Employee created = employeeService.create(request);
        return ResponseEntity.created(URI.create("/api/employees/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public Employee update(@PathVariable("id") Long id, @Valid @RequestBody EmployeeRequest request) {
        return employeeService.update(id, request);
    }

    @PatchMapping("/{id}/department")
    public Employee transfer(@PathVariable("id") Long id, @Valid @RequestBody DepartmentTransferRequest request) {
        return employeeService.transfer(id, request.department());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
