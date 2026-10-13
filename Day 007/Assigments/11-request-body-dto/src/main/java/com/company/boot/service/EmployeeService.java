package com.company.boot.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.company.boot.dto.AddressDto;
import com.company.boot.dto.EmployeeRequest;
import com.company.boot.dto.EmployeeResponse;
import com.company.boot.model.Employee;
import com.company.boot.repository.EmployeeRepository;

/**
 * Converts between DTOs and the internal model:
 *   EmployeeRequest  -> Employee (flat)  -> database
 *   database -> Employee -> EmployeeResponse (nested address, employeeCode)
 */
@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public EmployeeResponse create(EmployeeRequest request) {
        Employee employee = new Employee();
        employee.setName(request.getName().trim());
        employee.setEmail(request.getEmail().trim().toLowerCase());
        employee.setDepartment(request.getDepartment().trim());

        AddressDto address = request.getAddress();
        if (address != null) {
            employee.setStreet(address.getStreet());
            employee.setCity(address.getCity());
            employee.setState(address.getState());
            employee.setPincode(address.getPincode());
        }

        int id = employeeRepository.insert(employee);
        return employeeRepository.findById(id).map(this::toResponse).orElseThrow();
    }

    public Optional<EmployeeResponse> findById(int id) {
        return employeeRepository.findById(id).map(this::toResponse);
    }

    private EmployeeResponse toResponse(Employee employee) {
        AddressDto address = null;
        if (employee.getCity() != null) {
            address = new AddressDto(employee.getStreet(), employee.getCity(),
                    employee.getState(), employee.getPincode());
        }
        return new EmployeeResponse(
                employee.getEmployeeId(),
                String.format("EMP-%04d", employee.getEmployeeId()),
                employee.getName(),
                employee.getEmail(),
                employee.getDepartment(),
                address,
                employee.getCreatedAt());
    }
}
