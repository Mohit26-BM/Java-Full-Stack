package com.company.jdbc.service;

import java.util.List;

import com.company.jdbc.dao.EmployeeSortField;
import com.company.jdbc.exception.DepartmentNotFoundException;
import com.company.jdbc.exception.DuplicateEmployeeException;
import com.company.jdbc.exception.EmployeeNotFoundException;
import com.company.jdbc.exception.InvalidEmployeeException;
import com.company.jdbc.model.Department;
import com.company.jdbc.model.Employee;

public interface EmployeeService {

    int addEmployee(Employee employee)
            throws InvalidEmployeeException,
                   DepartmentNotFoundException,
                   DuplicateEmployeeException;

    Employee findEmployeeById(int employeeId)
            throws EmployeeNotFoundException;

    List<Employee> searchByDepartment(int departmentId)
            throws DepartmentNotFoundException;

    void updateContact(int employeeId, String email, String phone)
            throws EmployeeNotFoundException,
                   InvalidEmployeeException,
                   DuplicateEmployeeException;

    void deleteEmployee(int employeeId)
            throws EmployeeNotFoundException;

    /*
    pageNumber starts from 1.
    */
    List<Employee> getEmployeePage(EmployeeSortField sortField,
                                   boolean ascending,
                                   int pageNumber,
                                   int pageSize);

    int getTotalEmployees();

    List<Department> getAllDepartments();
}
