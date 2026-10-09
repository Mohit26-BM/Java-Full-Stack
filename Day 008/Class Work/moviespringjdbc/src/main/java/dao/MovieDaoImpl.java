package dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import model.Movie;

@Repository
public class MovieDaoImpl implements MovieDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public int addMovie(Movie movie) {

        String sql = "insert into movie values (?, ?, ?, ?)";

        return jdbcTemplate.update(
                sql,
                movie.getId(),
                movie.getName(),
                movie.getDirector(),
                movie.getPrice()
        );
    }

    @Override
    public List<Movie> getMovies() {

        String sql = "select * from movie";

        return jdbcTemplate.query(sql, new MovieRowMapper());
    }

    @Override
    public List<Movie> searchMovie(String name) {

        String sql = "select * from movie where name=?";

        return jdbcTemplate.query(
                sql,
                new MovieRowMapper(),
                name
        );
    }

    @Override
    public int deleteMovie(String name) {

        String sql = "delete from movie where name=?";

        return jdbcTemplate.update(sql, name);
    }
}