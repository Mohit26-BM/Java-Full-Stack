package service;

import java.util.List;
import model.Movie;

public interface MovieService {

    public int addMovie(Movie movie);

    public List<Movie> getMovies();

    public List<Movie> searchMovie(String name);

    public int deleteMovie(String name);
}