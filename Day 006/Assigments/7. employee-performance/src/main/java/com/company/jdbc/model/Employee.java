package com.company.jdbc.model;

public class Employee {

    private final int employeeId;
    private final String employeeName;

    public Employee(int employeeId, String employeeName) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    @Override
    public String toString() {
        return employeeId + "  " + employeeName;
    }
}
