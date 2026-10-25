package com.example.portal.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.portal.dto.EmployeeRequest;
import com.example.portal.dto.EmployeeResponse;
import com.example.portal.dto.PayrollResponse;
import com.example.portal.service.EmployeeService;

import jakarta.validation.Valid;

/**
 * Protected endpoints. A request only reaches these methods if Spring Security
 * accepted the token AND the user's role is allowed (rules in SecurityConfig).
 *
 * @AuthenticationPrincipal Jwt gives the validated token, e.g. jwt.getSubject() = "hr".
 */
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /** USER, HR, ADMIN */
    @GetMapping
    public List<EmployeeResponse> findAll() {
        return employeeService.findAll();
    }

    /** USER, HR, ADMIN */
    @GetMapping("/{id}")
    public EmployeeResponse findById(@PathVariable("id") Long id) {
        return employeeService.findById(id);
    }

    /** HR, ADMIN - salary information */
    @GetMapping("/{id}/payroll")
    public PayrollResponse payroll(@PathVariable("id") Long id) {
        return employeeService.getPayroll(id);
    }

    /** HR, ADMIN */
    @PostMapping
    public ResponseEntity<EmployeeResponse> create(@Valid @RequestBody EmployeeRequest request,
                                                   @AuthenticationPrincipal Jwt jwt) {
        EmployeeResponse created = employeeService.create(request, jwt.getSubject());
        return ResponseEntity.created(URI.create("/api/employees/" + created.id())).body(created);
    }

    /** HR, ADMIN */
    @PutMapping("/{id}")
    public EmployeeResponse update(@PathVariable("id") Long id, @Valid @RequestBody EmployeeRequest request,
                                   @AuthenticationPrincipal Jwt jwt) {
        return employeeService.update(id, request, jwt.getSubject());
    }

    /** ADMIN only */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id, @AuthenticationPrincipal Jwt jwt) {
        employeeService.delete(id, jwt.getSubject());
        return ResponseEntity.noContent().build();
    }
}
