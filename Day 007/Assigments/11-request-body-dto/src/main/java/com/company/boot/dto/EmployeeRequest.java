package com.company.boot.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for the POST /api/employees request body.
 *
 * Jackson maps the incoming JSON to this class:
 *   1. calls the no-arg constructor
 *   2. calls setName(..), setEmail(..) ... for each JSON property with the same name
 *   3. a nested JSON object {"address": {...}} becomes an AddressDto (optional extension)
 *
 * The constraints are checked when the controller parameter is marked @Valid.
 */
public class EmployeeRequest {

    @NotBlank(message = "name is required")
    @Size(max = 100, message = "name must be at most 100 characters")
    private String name;

    @NotBlank(message = "email is required")
    @Email(message = "email must be a valid e-mail address")
    private String email;

    @NotBlank(message = "department is required")
    private String department;

    /** Optional, but if present its own fields are validated too (@Valid cascades). */
    @Valid
    private AddressDto address;

    public EmployeeRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public AddressDto getAddress() {
        return address;
    }

    public void setAddress(AddressDto address) {
        this.address = address;
    }
}
