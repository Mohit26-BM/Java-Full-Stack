package com.company.methodsec.service;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.company.methodsec.exception.EmployeeNotFoundException;
import com.company.methodsec.model.Employee;
import com.company.methodsec.repository.EmployeeRepository;

@Service
public class EmployeeService {
    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);

    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    @PreAuthorize("isAuthenticated()")
    public List<Employee> findAll() {
        return repository.findAll();
    }

    @PostAuthorize("hasAnyRole('HR', 'ADMIN')"
            + " or returnObject.username() == authentication.name"
            + " or @employeeAccess.isManagerOf(returnObject.id(), authentication)")
    public Employee getEmployee(Long id) {
        log.info("Loading employee {}", id);
        return findOrThrow(id);
    }

    @PreAuthorize("hasAnyRole('HR', 'ADMIN') or @employeeAccess.isSelf(#id, authentication)")
    public BigDecimal getSalary(Long id) {
        return findOrThrow(id).salary();
    }

    @PreAuthorize("hasAnyRole('HR', 'ADMIN') and !@employeeAccess.isSelf(#id, authentication)")
    public Employee updateSalary(Long id, BigDecimal newSalary) {
        Employee updated = findOrThrow(id).withSalary(newSalary);
        repository.save(updated);
        log.info("Salary of employee {} changed", id);
        return updated;
    }

    @PreAuthorize("hasAnyRole('HR', 'ADMIN') or @employeeAccess.isManagerOf(#id, authentication)")
    public Employee updateDesignation(Long id, String designation) {
        Employee updated = findOrThrow(id).withDesignation(designation);
        repository.save(updated);
        return updated;
    }

    @PreAuthorize("hasAnyRole('HR', 'ADMIN') or (hasRole('MANAGER') and @employeeAccess.isSelf(#managerId, authentication))")
    public List<Employee> findTeam(Long managerId) {
        return repository.findByManagerId(managerId);
    }

    @Secured("ROLE_ADMIN")
    public void deleteEmployee(Long id) {
        if (!repository.delete(id)) {
            throw new EmployeeNotFoundException(id);
        }
        log.info("Employee {} deleted", id);
    }

    private Employee findOrThrow(Long id) {
        return repository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
    }
}
