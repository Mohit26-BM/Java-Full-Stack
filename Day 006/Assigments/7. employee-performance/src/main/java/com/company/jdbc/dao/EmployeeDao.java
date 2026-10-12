package com.company.jdbc.dao;

import java.util.List;

import com.company.jdbc.model.Employee;

public interface EmployeeDao {

    /*
    Returns null when no employee has this ID.
    */
    Employee findById(int employeeId);

    List<Employee> findAll();
}
