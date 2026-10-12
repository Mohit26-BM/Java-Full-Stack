package com.company.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.company.jdbc.config.DBConnection;
import com.company.jdbc.exception.DataAccessException;
import com.company.jdbc.model.Department;

public class DepartmentDaoImpl implements DepartmentDao {

    @Override
    public Department findById(int departmentId) {

        String sql = "SELECT department_id, department_name "
                   + "FROM departments "
                   + "WHERE department_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, departmentId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapRow(resultSet);
                }
            }

            /*
            null means "no such department".
            A real database error never reaches this line.
            */
            return null;

        } catch (SQLException e) {

            throw new DataAccessException(
                    "Unable to read department " + departmentId, e
            );
        }
    }

    @Override
    public List<Department> findAll() {

        List<Department> departments = new ArrayList<>();

        String sql = "SELECT department_id, department_name "
                   + "FROM departments "
                   + "ORDER BY department_id";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                departments.add(mapRow(resultSet));
            }

        } catch (SQLException e) {

            throw new DataAccessException(
                    "Unable to read departments", e
            );
        }

        return departments;
    }

    private Department mapRow(ResultSet resultSet) throws SQLException {

        return new Department(
                resultSet.getInt("department_id"),
                resultSet.getString("department_name")
        );
    }
}
