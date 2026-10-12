package com.company.jdbc.dao;

import java.util.List;

import com.company.jdbc.exception.DuplicateEmployeeException;
import com.company.jdbc.model.Employee;

public interface EmployeeDao {

    /*
    Returns the generated employee_id.
    */
    int add(Employee employee)
            throws DuplicateEmployeeException;

    /*
    Returns null when no employee has this ID.
    */
    Employee findById(int employeeId);

    List<Employee> findByDepartment(int departmentId);

    /*
    Returns true when exactly one row was updated.
    */
    boolean updateContact(int employeeId, String email, String phone)
            throws DuplicateEmployeeException;

    /*
    Returns true when exactly one row was deleted.
    */
    boolean delete(int employeeId);

    /*
    Optional challenge: pagination with LIMIT / OFFSET and sorting.
    */
    List<Employee> findPage(EmployeeSortField sortField,
                            boolean ascending,
                            int limit,
                            int offset);

    int countAll();
}
