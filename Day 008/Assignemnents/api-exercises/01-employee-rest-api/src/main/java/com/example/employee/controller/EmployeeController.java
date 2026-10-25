package com.example.employee.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.employee.dto.EmployeeRequest;
import com.example.employee.dto.EmployeeResponse;
import com.example.employee.dto.StatusUpdateRequest;
import com.example.employee.service.EmployeeService;

import jakarta.validation.Valid;

/**
 * The "employees" RESOURCE.
 *
 *   GET    /api/employees              200  list
 *   GET    /api/employees/{id}         200  one employee   | 404
 *   POST   /api/employees              201  created + Location header
 *   PUT    /api/employees/{id}         200  replaced       | 404
 *   PATCH  /api/employees/{id}/status  200  status changed | 404
 *   DELETE /api/employees/{id}         204  no body        | 404
 */
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public List<EmployeeResponse> findAll() {
        return employeeService.findAll();
    }

    @GetMapping("/{id}")
    public EmployeeResponse findById(@PathVariable("id") Long id) {
        return employeeService.findById(id);
    }

    /** 201 Created, with a Location header pointing to the new resource. */
    @PostMapping
    public ResponseEntity<EmployeeResponse> create(@Valid @RequestBody EmployeeRequest request) {
        EmployeeResponse created = employeeService.create(request);
        return ResponseEntity.created(URI.create("/api/employees/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public EmployeeResponse replace(@PathVariable("id") Long id, @Valid @RequestBody EmployeeRequest request) {
        return employeeService.replace(id, request);
    }

    @PatchMapping("/{id}/status")
    public EmployeeResponse updateStatus(@PathVariable("id") Long id, @Valid @RequestBody StatusUpdateRequest request) {
        return employeeService.updateStatus(id, request.status());
    }

    /** 204 No Content: the deletion worked and there is nothing to return. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
