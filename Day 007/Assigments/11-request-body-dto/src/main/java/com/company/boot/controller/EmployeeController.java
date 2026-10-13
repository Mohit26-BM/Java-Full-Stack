package com.company.boot.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.boot.dto.EmployeeRequest;
import com.company.boot.dto.EmployeeResponse;
import com.company.boot.service.EmployeeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * POST /api/employees
     * @RequestBody - Jackson converts the JSON body into an EmployeeRequest.
     * @Valid       - the DTO's constraints are checked BEFORE this method runs;
     *                failures go to GlobalExceptionHandler (400).
     */
    @PostMapping
    public ResponseEntity<EmployeeResponse> create(@Valid @RequestBody EmployeeRequest request) {
        EmployeeResponse response = employeeService.create(request);
        return ResponseEntity
                .created(URI.create("/api/employees/" + response.getId()))
                .body(response);
    }

    /** GET /api/employees/{id} - to read back what was saved. */
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> findById(@PathVariable("id") int id) {
        return employeeService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
