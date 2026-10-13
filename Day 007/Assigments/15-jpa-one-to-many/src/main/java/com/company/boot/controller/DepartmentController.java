package com.company.boot.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.company.boot.dto.DepartmentRequest;
import com.company.boot.dto.DepartmentResponse;
import com.company.boot.dto.EmployeeRequest;
import com.company.boot.dto.EmployeeResponse;
import com.company.boot.service.DepartmentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    /** Create a department. */
    @PostMapping
    public ResponseEntity<DepartmentResponse> create(@Valid @RequestBody DepartmentRequest request) {
        DepartmentResponse created = departmentService.createDepartment(request);
        return ResponseEntity.created(URI.create("/api/departments/" + created.id())).body(created);
    }

    /** Add an employee to a department. */
    @PostMapping("/{id}/employees")
    public ResponseEntity<EmployeeResponse> addEmployee(@PathVariable("id") Long id,
                                                        @Valid @RequestBody EmployeeRequest request) {
        EmployeeResponse created = departmentService.addEmployee(id, request);
        return ResponseEntity.created(URI.create("/api/employees/" + created.id())).body(created);
    }

    /** Retrieve the employees of a department. */
    @GetMapping("/{id}/employees")
    public List<EmployeeResponse> employees(@PathVariable("id") Long id) {
        return departmentService.getEmployees(id);
    }

    /** One department with its employees nested. */
    @GetMapping("/{id}")
    public DepartmentResponse findById(@PathVariable("id") Long id) {
        return departmentService.getDepartmentWithEmployees(id);
    }

    /** All departments with employees. ?fetch=lazy shows the N+1 problem; default uses a JOIN. */
    @GetMapping
    public List<DepartmentResponse> findAll(@RequestParam(name = "fetch", defaultValue = "graph") String fetch) {
        return departmentService.getAllDepartments(fetch);
    }

    @DeleteMapping("/{id}/employees/{employeeId}")
    public ResponseEntity<Void> removeEmployee(@PathVariable("id") Long id,
                                               @PathVariable("employeeId") Long employeeId) {
        departmentService.removeEmployee(id, employeeId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }
}
