package com.example.portal.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.portal.dto.EmployeeRequest;
import com.example.portal.dto.EmployeeResponse;
import com.example.portal.dto.PayrollResponse;
import com.example.portal.exception.DuplicateEmailException;
import com.example.portal.exception.EmployeeNotFoundException;
import com.example.portal.model.Employee;
import com.example.portal.repository.EmployeeRepository;

/**
 * Employee operations. WHO may call each method is decided in SecurityConfig;
 * this class only does the work and writes the AUDIT log for every change
 * (challenge extension: who did what, to which record).
 */
@Service
@Transactional(readOnly = true)
public class EmployeeService {

    private static final Logger audit = LoggerFactory.getLogger("AUDIT");

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<EmployeeResponse> findAll() {
        return employeeRepository.findAll().stream().map(EmployeeResponse::from).toList();
    }

    public EmployeeResponse findById(Long id) {
        return EmployeeResponse.from(getEmployee(id));
    }

    public PayrollResponse getPayroll(Long id) {
        return PayrollResponse.from(getEmployee(id));
    }

    @Transactional
    public EmployeeResponse create(EmployeeRequest request, String actor) {
        String email = request.email().trim().toLowerCase();
        if (employeeRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateEmailException(email);
        }
        Employee saved = employeeRepository.save(new Employee(request.name().trim(), email,
                request.department().trim(), request.designation().trim(), request.salary(), request.status()));
        audit.info("user={} action=CREATE_EMPLOYEE employeeId={} result=SUCCESS", actor, saved.getId());
        return EmployeeResponse.from(saved);
    }

    @Transactional
    public EmployeeResponse update(Long id, EmployeeRequest request, String actor) {
        Employee employee = getEmployee(id);
        String email = request.email().trim().toLowerCase();
        if (employeeRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new DuplicateEmailException(email);
        }
        employee.setName(request.name().trim());
        employee.setEmail(email);
        employee.setDepartment(request.department().trim());
        employee.setDesignation(request.designation().trim());
        employee.setSalary(request.salary());
        employee.setStatus(request.status());
        audit.info("user={} action=UPDATE_EMPLOYEE employeeId={} result=SUCCESS", actor, id);
        return EmployeeResponse.from(employee);
    }

    @Transactional
    public void delete(Long id, String actor) {
        Employee employee = getEmployee(id);
        employeeRepository.delete(employee);
        audit.info("user={} action=DELETE_EMPLOYEE employeeId={} email={} result=SUCCESS",
                actor, id, employee.getEmail());
    }

    private Employee getEmployee(Long id) {
        return employeeRepository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
    }
}
