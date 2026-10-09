package controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import model.Movie;
import service.MovieService;

@RestController
@RequestMapping("/movies")
public class MovieRestController {

    @Autowired
    MovieService movieService;

    @GetMapping
    public List<Movie> getMovies() {
        return movieService.getMovies();
    }

    @GetMapping("/search/{name}")
    public List<Movie> searchMovie(@PathVariable String name) {
        return movieService.searchMovie(name);
    }

    @PostMapping
    public Movie addMovie(@RequestBody Movie movie) {

        int k = movieService.addMovie(movie);

        if (k == 1) {
            return movie;
        }

        return null;
    }

    @DeleteMapping("/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public boolean deleteMovie(@PathVariable String name) {

        int k = movieService.deleteMovie(name);

        return k == 1;
    }
}