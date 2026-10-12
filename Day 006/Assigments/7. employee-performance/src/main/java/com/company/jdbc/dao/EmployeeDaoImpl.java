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
    public Employee findById(int employeeId) {

        String sql = "SELECT employee_id, employee_name "
                   + "FROM employees "
                   + "WHERE employee_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, employeeId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return new Employee(
                            resultSet.getInt("employee_id"),
                            resultSet.getString("employee_name")
                    );
                }
            }

            return null;

        } catch (SQLException e) {

            throw new DataAccessException(
                    "Unable to read employee " + employeeId, e
            );
        }
    }

    @Override
    public List<Employee> findAll() {

        List<Employee> employees = new ArrayList<>();

        String sql = "SELECT employee_id, employee_name "
                   + "FROM employees "
                   + "ORDER BY employee_id";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                employees.add(new Employee(
                        resultSet.getInt("employee_id"),
                        resultSet.getString("employee_name")
                ));
            }

        } catch (SQLException e) {

            throw new DataAccessException("Unable to read employees", e);
        }

        return employees;
    }
}
