package com.company.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.company.jdbc.config.DBConnection;
import com.company.jdbc.exception.DataAccessException;
import com.company.jdbc.model.Employee;

public class EmployeeDaoImpl implements EmployeeDao {

    @Override
    public List<Employee> findAll() {

        List<Employee> employees = new ArrayList<>();

        String sql = "SELECT employee_id, employee_code, employee_name "
                   + "FROM employees "
                   + "ORDER BY employee_code";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                employees.add(new Employee(
                        resultSet.getInt("employee_id"),
                        resultSet.getString("employee_code"),
                        resultSet.getString("employee_name")
                ));
            }

        } catch (SQLException e) {

            throw new DataAccessException("Unable to read employees", e);
        }

        return employees;
    }
}
