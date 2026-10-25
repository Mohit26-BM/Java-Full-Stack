package com.example.hospital.exception;

public class DepartmentNotFoundException extends NotFoundException {

    private static final long serialVersionUID = 1L;

    public DepartmentNotFoundException(Long id) {
        super("DEPARTMENT_NOT_FOUND", "Department not found with id " + id);
    }

    public DepartmentNotFoundException(String name) {
        super("DEPARTMENT_NOT_FOUND", "Department not found with name '" + name + "'");
    }
}
