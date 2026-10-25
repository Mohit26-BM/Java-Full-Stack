package com.company.boot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Body of POST /api/departments */
public class DepartmentRequest {

    @NotBlank(message = "name is required")
    @Size(max = 80, message = "name must be at most 80 characters")
    private String name;

    @Size(max = 80, message = "location must be at most 80 characters")
    private String location;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
