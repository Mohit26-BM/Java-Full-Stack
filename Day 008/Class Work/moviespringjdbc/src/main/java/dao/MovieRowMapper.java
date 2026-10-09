package dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import model.Movie;

public class MovieRowMapper implements RowMapper<Movie> {

    @Override
    public Movie mapRow(ResultSet rs, int rowNum) throws SQLException {

        return new Movie(
                rs.getString("id"),
                rs.getString("name"),
                rs.getString("director"),
                rs.getDouble("price")
        );
    }
}