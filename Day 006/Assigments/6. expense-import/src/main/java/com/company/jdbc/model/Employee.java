package com.company.jdbc.model;

public class Employee {

    private final int employeeId;
    private final String employeeCode;
    private final String employeeName;

    public Employee(int employeeId, String employeeCode, String employeeName) {
        this.employeeId = employeeId;
        this.employeeCode = employeeCode;
        this.employeeName = employeeName;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    @Override
    public String toString() {
        return String.format("%-4d %-8s %s", employeeId, employeeCode, employeeName);
    }
}
