package com.company.jdbc.dao;

import java.util.List;

import com.company.jdbc.model.Department;

public interface DepartmentDao {

    Department findById(int departmentId);

    List<Department> findAll();
}
