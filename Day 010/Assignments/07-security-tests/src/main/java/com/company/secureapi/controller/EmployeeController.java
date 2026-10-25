package com.company.secureapi.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.secureapi.dto.Dtos.CreateEmployeeRequest;
import com.company.secureapi.dto.Dtos.EmployeeDetail;
import com.company.secureapi.dto.Dtos.EmployeeSummary;
import com.company.secureapi.dto.Dtos.SalaryChange;
import com.company.secureapi.dto.Dtos.SalaryResponse;
import com.company.secureapi.dto.Dtos.SalaryUpdateRequest;
import com.company.secureapi.dto.Dtos.UpdateEmployeeRequest;
import com.company.secureapi.security.AuthenticatedUser;
import com.company.secureapi.service.EmployeeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @GetMapping
    public List<EmployeeSummary> findAll() {
        return service.findAll();
    }

    @GetMapping("/team")
    public List<EmployeeDetail> team(@AuthenticationPrincipal AuthenticatedUser me) {
        return service.myTeam(me);
    }

    @GetMapping("/{id}")
    public EmployeeDetail findById(@PathVariable("id") Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<EmployeeDetail> create(@Valid @RequestBody CreateEmployeeRequest request) {
        EmployeeDetail created = service.create(request);
        return ResponseEntity.created(URI.create("/api/employees/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public EmployeeDetail update(@PathVariable("id") Long id, @Valid @RequestBody UpdateEmployeeRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/salary")
    public SalaryResponse salary(@PathVariable("id") Long id) {
        return service.salary(id);
    }

    @PutMapping("/{id}/salary")
    public SalaryResponse updateSalary(@PathVariable("id") Long id, @Valid @RequestBody SalaryUpdateRequest request,
                                       @AuthenticationPrincipal AuthenticatedUser me) {
        return service.updateSalary(id, request.salary(), me.username());
    }

    @GetMapping("/{id}/salary/history")
    public List<SalaryChange> salaryHistory(@PathVariable("id") Long id) {
        return service.salaryHistory(id);
    }
}
