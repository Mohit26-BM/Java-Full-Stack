package com.example.hospital.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.hospital.dto.DepartmentRequest;
import com.example.hospital.dto.DepartmentResponse;
import com.example.hospital.service.DepartmentService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/** Module A - /api/departments */
@Tag(name = "Departments", description = "Module A - department management (cached reference data)")
@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    /** Create a department -> 201 Created */
    @PostMapping
    public ResponseEntity<DepartmentResponse> create(@Valid @RequestBody DepartmentRequest request) {
        DepartmentResponse created = departmentService.createDepartment(request);
        return ResponseEntity.created(URI.create("/api/departments/" + created.id())).body(created);
    }

    /** List departments: GET /api/departments  or  GET /api/departments?activeOnly=true */
    @GetMapping
    public List<DepartmentResponse> list(
            @RequestParam(name = "activeOnly", defaultValue = "false") boolean activeOnly) {
        return departmentService.findDepartments(activeOnly);
    }

    @GetMapping("/{id}")
    public DepartmentResponse findById(@PathVariable("id") Long id) {
        return departmentService.findById(id);
    }

    /** Find by name: GET /api/departments/search?name=Cardiology */
    @GetMapping("/search")
    public DepartmentResponse findByName(@RequestParam("name") String name) {
        return departmentService.findByName(name);
    }

    @PutMapping("/{id}")
    public DepartmentResponse update(@PathVariable("id") Long id, @Valid @RequestBody DepartmentRequest request) {
        return departmentService.updateDepartment(id, request);
    }

    @PatchMapping("/{id}/activate")
    public DepartmentResponse activate(@PathVariable("id") Long id) {
        return departmentService.activateDepartment(id);
    }

    @PatchMapping("/{id}/deactivate")
    public DepartmentResponse deactivate(@PathVariable("id") Long id) {
        return departmentService.deactivateDepartment(id);
    }
}
