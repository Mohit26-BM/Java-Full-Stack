package com.example.hospital.mapper;

import com.example.hospital.dto.DepartmentResponse;
import com.example.hospital.entity.Department;

/** Converts a Department entity into the DTO sent to the client. */
public final class DepartmentMapper {

    private DepartmentMapper() {
    }

    public static DepartmentResponse toResponse(Department department) {
        return new DepartmentResponse(
                department.getId(),
                department.getName(),
                department.getLocation(),
                department.getActive());
    }
}
