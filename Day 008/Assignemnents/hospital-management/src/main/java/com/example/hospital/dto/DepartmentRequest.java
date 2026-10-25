package com.example.hospital.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Body of POST /api/departments and PUT /api/departments/{id} */
public record DepartmentRequest(

        @NotBlank(message = "name is required")
        @Size(max = 100, message = "name must be at most 100 characters")
        String name,

        @Size(max = 150, message = "location must be at most 150 characters")
        String location) {
}
