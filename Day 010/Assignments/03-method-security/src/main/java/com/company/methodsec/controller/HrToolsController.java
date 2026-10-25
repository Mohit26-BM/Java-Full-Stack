package com.company.methodsec.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.methodsec.dto.EmployeeResponses.SalaryResponse;
import com.company.methodsec.service.EmployeeService;

@RestController
public class HrToolsController {
    private final EmployeeService employeeService;

    public HrToolsController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/api/tools/salary/{id}")
    public SalaryResponse salary(@PathVariable("id") Long id) {
        return new SalaryResponse(id, employeeService.getSalary(id));
    }

    @PostMapping("/api/tools/offboard/{id}")
    public ResponseEntity<Void> offboard(@PathVariable("id") Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
