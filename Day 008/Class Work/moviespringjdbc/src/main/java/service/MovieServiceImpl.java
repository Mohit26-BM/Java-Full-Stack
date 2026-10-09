package service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dao.MovieDao;
import model.Movie;

@Service
public class MovieServiceImpl implements MovieService {

    @Autowired
    MovieDao movieDao;

    @Override
    public int addMovie(Movie movie) {
        return movieDao.addMovie(movie);
    }

    @Override
    public List<Movie> getMovies() {
        return movieDao.getMovies();
    }

    @Override
    public List<Movie> searchMovie(String name) {
        return movieDao.searchMovie(name);
    }

    @Override
    public int deleteMovie(String name) {
        return movieDao.deleteMovie(name);
    }
}