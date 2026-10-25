package com.example.hospital.exception;

public class DepartmentInactiveException extends BusinessRuleException {

    private static final long serialVersionUID = 1L;

    public DepartmentInactiveException(String departmentName) {
        super("DEPARTMENT_INACTIVE", "Department '" + departmentName + "' is inactive");
    }
}
