package com.example.hospital.exception;

public class DuplicateDepartmentException extends ConflictException {

    private static final long serialVersionUID = 1L;

    public DuplicateDepartmentException(String name) {
        super("DUPLICATE_DEPARTMENT", "A department named '" + name + "' already exists");
    }
}
