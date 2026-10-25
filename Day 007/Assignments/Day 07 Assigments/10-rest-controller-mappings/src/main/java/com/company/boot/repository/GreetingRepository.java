package com.company.boot.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.company.boot.model.Greeting;

/** Data access for a10_greetings using the auto-configured JdbcTemplate. */
@Repository
public class GreetingRepository {

    private static final String INSERT_SQL =
            "INSERT INTO a10_greetings (name, message) VALUES (?, ?)";
    private static final String SELECT_ALL_SQL =
            "SELECT greeting_id, name, message, created_at FROM a10_greetings ORDER BY greeting_id";
    private static final String SELECT_BY_ID_SQL =
            "SELECT greeting_id, name, message, created_at FROM a10_greetings WHERE greeting_id = ?";

    private static final RowMapper<Greeting> ROW_MAPPER = (rs, rowNum) -> new Greeting(
            rs.getInt("greeting_id"),
            rs.getString("name"),
            rs.getString("message"),
            rs.getTimestamp("created_at").toLocalDateTime());

    private final JdbcTemplate jdbcTemplate;

    public GreetingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /** Inserts a row and returns the generated greeting_id. */
    public int insert(String name, String message) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setString(2, message);
            return ps;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    public List<Greeting> findAll() {
        return jdbcTemplate.query(SELECT_ALL_SQL, ROW_MAPPER);
    }

    public Optional<Greeting> findById(int id) {
        return jdbcTemplate.query(SELECT_BY_ID_SQL, ROW_MAPPER, id).stream().findFirst();
    }
}
