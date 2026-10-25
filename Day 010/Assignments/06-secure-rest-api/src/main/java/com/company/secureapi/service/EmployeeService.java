package com.company.secureapi.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.secureapi.dto.Dtos.CreateEmployeeRequest;
import com.company.secureapi.dto.Dtos.EmployeeDetail;
import com.company.secureapi.dto.Dtos.EmployeeSummary;
import com.company.secureapi.dto.Dtos.SalaryChange;
import com.company.secureapi.dto.Dtos.SalaryResponse;
import com.company.secureapi.dto.Dtos.UpdateEmployeeRequest;
import com.company.secureapi.entity.Employee;
import com.company.secureapi.entity.SalaryAudit;
import com.company.secureapi.exception.ApiException;
import com.company.secureapi.repository.EmployeeRepository;
import com.company.secureapi.repository.SalaryAuditRepository;
import com.company.secureapi.security.AuthenticatedUser;

@Service
public class EmployeeService {
    private static final Logger auditLog = LoggerFactory.getLogger("security.audit");

    private final EmployeeRepository employees;
    private final SalaryAuditRepository audits;

    public EmployeeService(EmployeeRepository employees, SalaryAuditRepository audits) {
        this.employees = employees;
        this.audits = audits;
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public List<EmployeeSummary> findAll() {
        return employees.findAllByOrderByIdAsc().stream().map(EmployeeSummary::of).toList();
    }

    @PreAuthorize("hasAnyRole('HR','ADMIN') or @access.isSelf(#id, authentication) or @access.isManagerOf(#id, authentication)")
    @Transactional(readOnly = true)
    public EmployeeDetail findById(Long id) {
        return EmployeeDetail.of(find(id));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @Transactional(readOnly = true)
    public List<EmployeeDetail> myTeam(AuthenticatedUser me) {
        return employees.findByManagerIdOrderByIdAsc(me.employeeId()).stream().map(EmployeeDetail::of).toList();
    }

    @PreAuthorize("hasAnyRole('HR','ADMIN') or @access.isSelf(#id, authentication)")
    @Transactional(readOnly = true)
    public SalaryResponse salary(Long id) {
        return new SalaryResponse(id, find(id).getSalary());
    }

    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @Transactional
    public EmployeeDetail create(CreateEmployeeRequest request) {
        if (employees.existsByEmail(request.email())) {
            throw ApiException.duplicate("Email " + request.email() + " is already used");
        }
        checkManager(request.managerId());
        Employee saved = employees.save(new Employee(request.name(), request.email(), request.department(),
                request.designation(), request.salary(), request.managerId()));
        return EmployeeDetail.of(saved);
    }

    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @Transactional
    public EmployeeDetail update(Long id, UpdateEmployeeRequest request) {
        Employee employee = find(id);
        if (employees.existsByEmailAndIdNot(request.email(), id)) {
            throw ApiException.duplicate("Email " + request.email() + " is already used");
        }
        checkManager(request.managerId());
        employee.update(request.name(), request.email(), request.department(), request.designation(),
                request.managerId(), request.status());
        return EmployeeDetail.of(employee);
    }

    @PreAuthorize("hasAnyRole('HR','ADMIN') and !@access.isSelf(#id, authentication)")
    @Transactional
    public SalaryResponse updateSalary(Long id, java.math.BigDecimal newSalary, String changedBy) {
        Employee employee = find(id);
        audits.save(new SalaryAudit(id, employee.getSalary(), newSalary, changedBy, LocalDateTime.now()));
        auditLog.info("audit_event=SALARY_CHANGED employee={} old={} new={} by={}", id, employee.getSalary(), newSalary, changedBy);
        employee.setSalary(newSalary);
        return new SalaryResponse(id, newSalary);
    }

    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @Transactional(readOnly = true)
    public List<SalaryChange> salaryHistory(Long id) {
        find(id);
        return audits.findByEmployeeIdOrderByIdAsc(id).stream().map(SalaryChange::of).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void delete(Long id) {
        employees.delete(find(id));
    }

    private Employee find(Long id) {
        return employees.findById(id).orElseThrow(() -> ApiException.employeeNotFound(id));
    }

    private void checkManager(Long managerId) {
        if (managerId != null && !employees.existsById(managerId)) {
            throw ApiException.employeeNotFound(managerId);
        }
    }
}
