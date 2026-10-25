package com.company.boot.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.company.boot.dto.EmployeeResponse;
import com.company.boot.service.DepartmentService;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final DepartmentService departmentService;

    public EmployeeController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping("/{id}")
    public EmployeeResponse findById(@PathVariable("id") Long id) {
        return departmentService.getEmployee(id);
    }

    /** ?fetch=lazy -> 1 + N queries for department names; default -> 1 JOIN query. */
    @GetMapping
    public List<EmployeeResponse> findAll(@RequestParam(name = "fetch", defaultValue = "graph") String fetch) {
        return departmentService.getAllEmployees(fetch);
    }
}
