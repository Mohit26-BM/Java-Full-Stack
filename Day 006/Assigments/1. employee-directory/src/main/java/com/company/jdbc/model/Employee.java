package com.company.jdbc.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/*
Plain model class.

Validation is done in EmployeeServiceImpl BEFORE anything
is sent to the database.

phone, departmentId, salary and joiningDate are nullable
in the table, so wrapper types / objects are used for them.
*/
public class Employee {

    private int employeeId;
    private String employeeCode;
    private String employeeName;
    private String email;
    private String phone;
    private Integer departmentId;
    private BigDecimal salary;
    private LocalDate joiningDate;

    /*
    Filled only when reading from the database (JOIN with departments).
    It is used for display and is never written back.
    */
    private String departmentName;

    public Employee() {
    }

    public Employee(String employeeCode,
                    String employeeName,
                    String email,
                    String phone,
                    Integer departmentId,
                    BigDecimal salary,
                    LocalDate joiningDate) {

        this.employeeCode = employeeCode;
        this.employeeName = employeeName;
        this.email = email;
        this.phone = phone;
        this.departmentId = departmentId;
        this.salary = salary;
        this.joiningDate = joiningDate;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public LocalDate getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(LocalDate joiningDate) {
        this.joiningDate = joiningDate;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    @Override
    public String toString() {

        return String.format(
                "%-4d %-8s %-18s %-28s %-11s %-16s %12s  %s",
                employeeId,
                employeeCode,
                employeeName,
                email,
                phone == null ? "-" : phone,
                departmentName == null ? "-" : departmentName,
                salary == null ? "-" : "Rs. " + salary,
                joiningDate == null ? "-" : joiningDate
        );
    }
}
