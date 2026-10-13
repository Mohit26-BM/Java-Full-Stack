package com.company.boot.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.company.boot.model.Employee;

/** Data access for a11_employees using the auto-configured JdbcTemplate. */
@Repository
public class EmployeeRepository {

    private static final String INSERT_SQL =
            "INSERT INTO a11_employees (name, email, department, street, city, state, pincode) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_BY_ID_SQL =
            "SELECT employee_id, name, email, department, street, city, state, pincode, created_at "
            + "FROM a11_employees WHERE employee_id = ?";

    private static final RowMapper<Employee> ROW_MAPPER = (rs, rowNum) -> {
        Employee employee = new Employee();
        employee.setEmployeeId(rs.getInt("employee_id"));
        employee.setName(rs.getString("name"));
        employee.setEmail(rs.getString("email"));
        employee.setDepartment(rs.getString("department"));
        employee.setStreet(rs.getString("street"));
        employee.setCity(rs.getString("city"));
        employee.setState(rs.getString("state"));
        employee.setPincode(rs.getString("pincode"));
        employee.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return employee;
    };

    private final JdbcTemplate jdbcTemplate;

    public EmployeeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Inserts the employee and returns the generated employee_id.
     * A duplicate e-mail violates the UNIQUE key -> Spring throws DuplicateKeyException.
     */
    public int insert(Employee employee) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, employee.getName());
            ps.setString(2, employee.getEmail());
            ps.setString(3, employee.getDepartment());
            ps.setString(4, employee.getStreet());
            ps.setString(5, employee.getCity());
            ps.setString(6, employee.getState());
            ps.setString(7, employee.getPincode());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    public Optional<Employee> findById(int employeeId) {
        return jdbcTemplate.query(SELECT_BY_ID_SQL, ROW_MAPPER, employeeId).stream().findFirst();
    }
}
