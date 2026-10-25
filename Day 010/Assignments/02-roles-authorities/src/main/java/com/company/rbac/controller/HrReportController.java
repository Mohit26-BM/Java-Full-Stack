package com.company.rbac.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.rbac.service.EmployeeService;

/** The ADMIN+HR endpoint: /api/hr/** is hasAnyRole("HR", "ADMIN"). */
@RestController
public class HrReportController {

    private final EmployeeService employeeService;

    public HrReportController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/api/hr/reports/headcount")
    public Map<String, Long> headcount() {
        return employeeService.headcountByDepartment();
    }
}
