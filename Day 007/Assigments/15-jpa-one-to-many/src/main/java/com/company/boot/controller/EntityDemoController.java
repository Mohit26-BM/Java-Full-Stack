package com.company.boot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.boot.entity.Department;
import com.company.boot.exception.ResourceNotFoundException;
import com.company.boot.repository.DepartmentRepository;

/**
 * DEMONSTRATION ONLY - what goes wrong when JPA ENTITIES are returned as JSON.
 * The real API (DepartmentController) returns DTOs instead.
 */
@RestController
@RequestMapping("/api/demo/entity")
public class EntityDemoController {

    private final DepartmentRepository departmentRepository;

    public EntityDemoController(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    /**
     * Problem 1: employees is LAZY and the transaction/session is already closed
     * when Jackson serialises -> LazyInitializationException.
     */
    @GetMapping("/departments/{id}/lazy")
    public Department lazyEntity(@PathVariable("id") Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", id));
    }

    /**
     * Problem 2: employees ARE loaded, so Jackson follows
     * department -> employees -> employee.department -> employees -> ... forever.
     */
    @GetMapping("/departments/{id}/fetched")
    public Department fetchedEntity(@PathVariable("id") Long id) {
        return departmentRepository.findWithEmployeesById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", id));
    }
}
