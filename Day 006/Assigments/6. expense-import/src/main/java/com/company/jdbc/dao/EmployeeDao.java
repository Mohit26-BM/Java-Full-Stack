package com.company.jdbc.dao;

import java.util.List;

import com.company.jdbc.model.Employee;

public interface EmployeeDao {

    List<Employee> findAll();
}
