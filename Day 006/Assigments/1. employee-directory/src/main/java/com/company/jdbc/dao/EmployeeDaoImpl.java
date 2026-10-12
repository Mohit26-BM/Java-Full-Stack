package com.company.jdbc.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.company.jdbc.config.DBConnection;
import com.company.jdbc.exception.DataAccessException;
import com.company.jdbc.exception.DuplicateEmployeeException;
import com.company.jdbc.model.Employee;

public class EmployeeDaoImpl implements EmployeeDao {

    /*
    MySQL error code for "Duplicate entry ... for key ...".
    */
    private static final int MYSQL_DUPLICATE_KEY = 1062;

    /*
    Common SELECT used by every read method.
    LEFT JOIN so employees without a department are still returned.
    */
    private static final String SELECT_EMPLOYEE =
            "SELECT e.employee_id, e.employee_code, e.employee_name, "
          + "e.email, e.phone, e.department_id, e.salary, e.joining_date, "
          + "d.department_name "
          + "FROM employees e "
          + "LEFT JOIN departments d "
          + "ON e.department_id = d.department_id ";

    @Override
    public int add(Employee employee)
            throws DuplicateEmployeeException {

        String sql = "INSERT INTO employees "
                   + "(employee_code, employee_name, email, phone, "
                   + "department_id, salary, joining_date) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, employee.getEmployeeCode());
            statement.setString(2, employee.getEmployeeName());
            statement.setString(3, employee.getEmail());
            setNullableString(statement, 4, employee.getPhone());
            setNullableInteger(statement, 5, employee.getDepartmentId());
            setNullableDecimal(statement, 6, employee.getSalary());

            if (employee.getJoiningDate() == null) {
                statement.setNull(7, Types.DATE);
            } else {
                statement.setDate(7, Date.valueOf(employee.getJoiningDate()));
            }

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {

                if (keys.next()) {
                    return keys.getInt(1);
                }
            }

            throw new DataAccessException(
                    "Employee was inserted but no ID was returned.", null
            );

        } catch (SQLIntegrityConstraintViolationException e) {

            throw toDuplicateException(e);

        } catch (SQLException e) {

            throw new DataAccessException(
                    "Unable to add employee", e
            );
        }
    }

    @Override
    public Employee findById(int employeeId) {

        String sql = SELECT_EMPLOYEE
                   + "WHERE e.employee_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, employeeId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapRow(resultSet);
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
    public List<Employee> findByDepartment(int departmentId) {

        String sql = SELECT_EMPLOYEE
                   + "WHERE e.department_id = ? "
                   + "ORDER BY e.employee_name";

        List<Employee> employees = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, departmentId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    employees.add(mapRow(resultSet));
                }
            }

        } catch (SQLException e) {

            throw new DataAccessException(
                    "Unable to search department " + departmentId, e
            );
        }

        return employees;
    }

    @Override
    public boolean updateContact(int employeeId, String email, String phone)
            throws DuplicateEmployeeException {

        /*
        WHERE employee_id = ? guarantees that only the
        requested employee is changed.
        */
        String sql = "UPDATE employees "
                   + "SET email = ?, phone = ? "
                   + "WHERE employee_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, email);
            setNullableString(statement, 2, phone);
            statement.setInt(3, employeeId);

            return statement.executeUpdate() == 1;

        } catch (SQLIntegrityConstraintViolationException e) {

            throw toDuplicateException(e);

        } catch (SQLException e) {

            throw new DataAccessException(
                    "Unable to update employee " + employeeId, e
            );
        }
    }

    @Override
    public boolean delete(int employeeId) {

        String sql = "DELETE FROM employees "
                   + "WHERE employee_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, employeeId);

            return statement.executeUpdate() == 1;

        } catch (SQLException e) {

            throw new DataAccessException(
                    "Unable to delete employee " + employeeId, e
            );
        }
    }

    @Override
    public List<Employee> findPage(EmployeeSortField sortField,
                                   boolean ascending,
                                   int limit,
                                   int offset) {

        /*
        The column and direction come from an enum / boolean,
        never from user text, so concatenating them is safe.
        employee_id is added as a tie-breaker so the page
        order is always stable.
        */
        String sql = SELECT_EMPLOYEE
                   + "ORDER BY e." + sortField.getColumnName()
                   + (ascending ? " ASC" : " DESC")
                   + ", e.employee_id "
                   + "LIMIT ? OFFSET ?";

        List<Employee> employees = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, limit);
            statement.setInt(2, offset);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    employees.add(mapRow(resultSet));
                }
            }

        } catch (SQLException e) {

            throw new DataAccessException(
                    "Unable to read employee page", e
            );
        }

        return employees;
    }

    @Override
    public int countAll() {

        String sql = "SELECT COUNT(*) FROM employees";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            resultSet.next();

            return resultSet.getInt(1);

        } catch (SQLException e) {

            throw new DataAccessException(
                    "Unable to count employees", e
            );
        }
    }

    /*
    Converts a MySQL UNIQUE violation into a clear message
    that says WHICH value is duplicated.

    Example MySQL message:
    Duplicate entry 'EMP001' for key 'employees.employee_code'
    */
    private DuplicateEmployeeException toDuplicateException(
            SQLIntegrityConstraintViolationException e) {

        if (e.getErrorCode() != MYSQL_DUPLICATE_KEY) {

            /*
            Some other constraint (for example a foreign key).
            The service checks the department first, so this
            should not normally happen.
            */
            throw new DataAccessException(
                    "Database constraint violated: " + e.getMessage(), e
            );
        }

        String message = e.getMessage();

        if (message.contains("employee_code")) {
            return new DuplicateEmployeeException(
                    "An employee with this employee code already exists."
            );
        }

        if (message.contains("email")) {
            return new DuplicateEmployeeException(
                    "An employee with this email already exists."
            );
        }

        return new DuplicateEmployeeException(
                "Duplicate value: " + message
        );
    }

    private Employee mapRow(ResultSet resultSet) throws SQLException {

        Employee employee = new Employee();

        employee.setEmployeeId(resultSet.getInt("employee_id"));
        employee.setEmployeeCode(resultSet.getString("employee_code"));
        employee.setEmployeeName(resultSet.getString("employee_name"));
        employee.setEmail(resultSet.getString("email"));
        employee.setPhone(resultSet.getString("phone"));

        /*
        getInt() returns 0 for SQL NULL, so wasNull() is checked.
        */
        int departmentId = resultSet.getInt("department_id");
        employee.setDepartmentId(
                resultSet.wasNull() ? null : departmentId
        );

        employee.setSalary(resultSet.getBigDecimal("salary"));

        Date joiningDate = resultSet.getDate("joining_date");
        employee.setJoiningDate(
                joiningDate == null ? null : joiningDate.toLocalDate()
        );

        employee.setDepartmentName(resultSet.getString("department_name"));

        return employee;
    }

    private void setNullableString(PreparedStatement statement,
                                   int index,
                                   String value) throws SQLException {

        if (value == null) {
            statement.setNull(index, Types.VARCHAR);
        } else {
            statement.setString(index, value);
        }
    }

    private void setNullableInteger(PreparedStatement statement,
                                    int index,
                                    Integer value) throws SQLException {

        if (value == null) {
            statement.setNull(index, Types.INTEGER);
        } else {
            statement.setInt(index, value);
        }
    }

    private void setNullableDecimal(PreparedStatement statement,
                                    int index,
                                    BigDecimal value) throws SQLException {

        if (value == null) {
            statement.setNull(index, Types.DECIMAL);
        } else {
            statement.setBigDecimal(index, value);
        }
    }
}
