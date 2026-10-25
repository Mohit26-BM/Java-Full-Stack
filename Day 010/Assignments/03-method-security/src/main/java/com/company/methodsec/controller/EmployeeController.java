package com.company.methodsec.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.company.methodsec.dto.EmployeeRequests.DesignationUpdate;
import com.company.methodsec.dto.EmployeeRequests.SalaryUpdate;
import com.company.methodsec.dto.EmployeeResponses.EmployeeDetail;
import com.company.methodsec.dto.EmployeeResponses.EmployeeSummary;
import com.company.methodsec.dto.EmployeeResponses.SalaryResponse;
import com.company.methodsec.service.EmployeeService;

import jakarta.validation.Valid;

/**
 * The main HR API. No authorization code here: the URL chain only requires a
 * login (plus ADMIN for DELETE), and each service method enforces its own rule.
 */
@RestController
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/api/employees")
    public List<EmployeeSummary> findAll() {
        return employeeService.findAll().stream().map(EmployeeSummary::of).toList();
    }

    @GetMapping("/api/employees/{id}")
    public EmployeeDetail findById(@PathVariable("id") Long id) {
        return EmployeeDetail.of(employeeService.getEmployee(id));
    }

    @GetMapping("/api/employees/{id}/salary")
    public SalaryResponse salary(@PathVariable("id") Long id) {
        return new SalaryResponse(id, employeeService.getSalary(id));
    }

    @PutMapping("/api/employees/{id}/salary")
    public SalaryResponse updateSalary(@PathVariable("id") Long id, @Valid @RequestBody SalaryUpdate request) {
        return new SalaryResponse(id, employeeService.updateSalary(id, request.salary()).salary());
    }

    @PatchMapping("/api/employees/{id}/designation")
    public EmployeeDetail updateDesignation(@PathVariable("id") Long id, @Valid @RequestBody DesignationUpdate request) {
        return EmployeeDetail.of(employeeService.updateDesignation(id, request.designation()));
    }

    @GetMapping("/api/managers/{managerId}/team")
    public List<EmployeeSummary> team(@PathVariable("managerId") Long managerId) {
        return employeeService.findTeam(managerId).stream().map(EmployeeSummary::of).toList();
    }

    @DeleteMapping("/api/employees/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
