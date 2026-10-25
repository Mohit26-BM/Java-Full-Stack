package com.example.employee.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.employee.dto.EmployeeRequest;
import com.example.employee.dto.EmployeeResponse;
import com.example.employee.exception.EmployeeNotFoundException;
import com.example.employee.model.Employee;
import com.example.employee.model.EmployeeStatus;
import com.example.employee.repository.EmployeeRepository;

/** Employee operations. Converts between DTOs and the Employee model. */
@Service
public class EmployeeService {

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

    public EmployeeResponse create(EmployeeRequest request) {
        Employee created = employeeRepository.save(request.name().trim(), request.email().trim().toLowerCase(),
                request.department().trim(), request.salary(), request.status());
        return EmployeeResponse.from(created);
    }

    /** PUT = REPLACE: every field is overwritten with the values from the request. */
    public EmployeeResponse replace(Long id, EmployeeRequest request) {
        Employee employee = getEmployee(id);
        employee.setName(request.name().trim());
        employee.setEmail(request.email().trim().toLowerCase());
        employee.setDepartment(request.department().trim());
        employee.setSalary(request.salary());
        employee.setStatus(request.status());
        return EmployeeResponse.from(employee);
    }

    /** PATCH = PARTIAL update: only the status changes, every other field stays as it is. */
    public EmployeeResponse updateStatus(Long id, EmployeeStatus status) {
        Employee employee = getEmployee(id);
        employee.setStatus(status);
        return EmployeeResponse.from(employee);
    }

    public void delete(Long id) {
        if (!employeeRepository.deleteById(id)) {
            throw new EmployeeNotFoundException(id);
        }
    }

    private Employee getEmployee(Long id) {
        return employeeRepository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
    }
}
