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

/**
 * The business methods, each carrying its OWN authorization rule.
 *
 * Spring wraps this bean in a proxy. Every call from outside goes through the
 * proxy, which checks the annotation first (@PreAuthorize, @Secured) or after
 * the method returned (@PostAuthorize), and throws AccessDeniedException when
 * the rule fails. Whoever calls the method (any controller, a job, a test)
 * gets the same protection.
 *
 * Inside the expressions:
 *   #id               a method parameter, by name
 *   returnObject      the value the method returned (@PostAuthorize only)
 *   authentication    the logged-in user (from the SecurityContext)
 *   @employeeAccess   a Spring bean, here the custom ownership rules
 */
@Service
public class EmployeeService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);

    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    /** Anyone logged in may list the directory (the controller shows it without salaries). */
    @PreAuthorize("isAuthenticated()")
    public List<Employee> findAll() {
        return repository.findAll();
    }

    /**
     * @PostAuthorize: the method RUNS FIRST, then the rule is checked against the
     * returned employee. HR and ADMIN see anyone; others only their own record or
     * one of their direct reports.
     */
    @PostAuthorize("hasAnyRole('HR', 'ADMIN')"
            + " or returnObject.username() == authentication.name"
            + " or @employeeAccess.isManagerOf(returnObject.id(), authentication)")
    public Employee getEmployee(Long id) {
        log.info("Loading employee {}", id);              // printed even when the caller is then denied
        return findOrThrow(id);
    }

    /** Parameter-based ownership: HR/ADMIN, or the employee asking for THEIR OWN salary. */
    @PreAuthorize("hasAnyRole('HR', 'ADMIN') or @employeeAccess.isSelf(#id, authentication)")
    public BigDecimal getSalary(Long id) {
        return findOrThrow(id).salary();
    }

    /** HR or ADMIN only, and never on their own record (separation of duties). */
    @PreAuthorize("hasAnyRole('HR', 'ADMIN') and !@employeeAccess.isSelf(#id, authentication)")
    public Employee updateSalary(Long id, BigDecimal newSalary) {
        Employee updated = findOrThrow(id).withSalary(newSalary);
        repository.save(updated);
        log.info("Salary of employee {} changed", id);
        return updated;
    }

    /** Manager ownership: HR/ADMIN, or the employee's own line manager. */
    @PreAuthorize("hasAnyRole('HR', 'ADMIN') or @employeeAccess.isManagerOf(#id, authentication)")
    public Employee updateDesignation(Long id, String designation) {
        Employee updated = findOrThrow(id).withDesignation(designation);
        repository.save(updated);
        return updated;
    }

    /** A manager may list only THEIR OWN team; HR and ADMIN may list any team. */
    @PreAuthorize("hasAnyRole('HR', 'ADMIN') or (hasRole('MANAGER') and @employeeAccess.isSelf(#managerId, authentication))")
    public List<Employee> findTeam(Long managerId) {
        return repository.findByManagerId(managerId);
    }

    /**
     * @Secured: the older, simpler annotation. It takes authority names (with the
     * ROLE_ prefix written out) and nothing else: no parameters, no and/or, no beans.
     * Equivalent here to @PreAuthorize("hasRole('ADMIN')").
     */
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
