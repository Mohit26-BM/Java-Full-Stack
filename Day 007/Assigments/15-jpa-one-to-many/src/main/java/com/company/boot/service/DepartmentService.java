package com.company.boot.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.boot.dto.DepartmentRequest;
import com.company.boot.dto.DepartmentResponse;
import com.company.boot.dto.EmployeeRequest;
import com.company.boot.dto.EmployeeResponse;
import com.company.boot.entity.Department;
import com.company.boot.entity.Employee;
import com.company.boot.exception.DuplicateResourceException;
import com.company.boot.exception.ResourceNotFoundException;
import com.company.boot.repository.DepartmentRepository;
import com.company.boot.repository.EmployeeRepository;

/**
 * Entities never leave this class: every method returns DTOs, built INSIDE the
 * transaction while lazy associations can still be loaded.
 */
@Service
@Transactional(readOnly = true)
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    public DepartmentService(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public DepartmentResponse createDepartment(DepartmentRequest request) {
        String name = request.getName().trim();
        if (departmentRepository.existsByNameIgnoreCase(name)) {
            throw new DuplicateResourceException("Department '" + name + "' already exists");
        }
        Department saved = departmentRepository.save(new Department(name, request.getLocation()));
        return DepartmentResponse.from(saved);
    }

    /**
     * Adds an employee THROUGH the department: no employeeRepository.save() call.
     * cascade = ALL on Department.employees makes Hibernate INSERT the new employee.
     */
    @Transactional
    public EmployeeResponse addEmployee(Long departmentId, EmployeeRequest request) {
        Department department = getDepartment(departmentId);
        String email = request.getEmail().trim().toLowerCase();
        if (employeeRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateResourceException("An employee with email '" + email + "' already exists");
        }
        Employee employee = new Employee(request.getName().trim(), email, request.getSalary());
        department.addEmployee(employee);
        departmentRepository.flush();           // run the cascaded INSERT now so the generated id is available
        return EmployeeResponse.from(employee);
    }

    public List<EmployeeResponse> getEmployees(Long departmentId) {
        getDepartment(departmentId);            // 404 if the department does not exist
        return employeeRepository.findByDepartmentIdOrderByIdAsc(departmentId).stream()
                .map(EmployeeResponse::from)
                .toList();
    }

    public DepartmentResponse getDepartmentWithEmployees(Long departmentId) {
        return departmentRepository.findWithEmployeesById(departmentId)
                .map(DepartmentResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Department", departmentId));
    }

    /**
     * OPTIONAL EXTENSION - compare loading strategies for the same result.
     * lazy  : findAll() then touch each LAZY collection -> 1 + N queries
     * graph : one LEFT JOIN query via @EntityGraph        -> 1 query
     */
    public List<DepartmentResponse> getAllDepartments(String fetch) {
        List<Department> departments = "lazy".equalsIgnoreCase(fetch)
                ? departmentRepository.findAll(org.springframework.data.domain.Sort.by("id"))
                : departmentRepository.findAllWithEmployees();
        return departments.stream().map(DepartmentResponse::from).toList();
    }

    public EmployeeResponse getEmployee(Long employeeId) {
        return employeeRepository.findById(employeeId)
                .map(EmployeeResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", employeeId));
    }

    /** OPTIONAL EXTENSION - all employees with department name: lazy (N+1) vs join. */
    public List<EmployeeResponse> getAllEmployees(String fetch) {
        List<Employee> employees = "lazy".equalsIgnoreCase(fetch)
                ? employeeRepository.findByOrderByIdAsc()
                : employeeRepository.findAllByOrderByIdAsc();
        return employees.stream().map(EmployeeResponse::from).toList();
    }

    /** Removing from the collection is enough: orphanRemoval = true issues the DELETE. */
    @Transactional
    public void removeEmployee(Long departmentId, Long employeeId) {
        Department department = getDepartment(departmentId);
        Employee employee = department.getEmployees().stream()
                .filter(e -> e.getId().equals(employeeId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Employee in department " + departmentId, employeeId));
        department.removeEmployee(employee);
    }

    /** cascade = ALL includes REMOVE: the department's employees are deleted first. */
    @Transactional
    public void deleteDepartment(Long departmentId) {
        departmentRepository.delete(getDepartment(departmentId));
    }

    private Department getDepartment(Long departmentId) {
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department", departmentId));
    }
}
