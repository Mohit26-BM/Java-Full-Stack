package com.company.boot.dto;

import java.time.LocalDateTime;

/**
 * DTO for the response. Jackson serialises it through its getters.
 *
 * It is deliberately NOT the same as the request:
 * - adds server-generated data (id, employeeCode, createdAt)
 * - contains only what the client should see (no internal columns)
 */
public class EmployeeResponse {

    private final int id;
    private final String employeeCode;
    private final String name;
    private final String email;
    private final String department;
    private final AddressDto address;
    private final LocalDateTime createdAt;

    public EmployeeResponse(int id, String employeeCode, String name, String email,
                            String department, AddressDto address, LocalDateTime createdAt) {
        this.id = id;
        this.employeeCode = employeeCode;
        this.name = name;
        this.email = email;
        this.department = department;
        this.address = address;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getDepartment() {
        return department;
    }

    public AddressDto getAddress() {
        return address;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
