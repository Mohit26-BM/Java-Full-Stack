/*package controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import model.Movie;
import service.MovieService;

@Controller
public class MovieController {

    @Autowired
    MovieService movieService;

    // Display all movies
    @GetMapping("/")
    public String getMovies(Model model) {

        List<Movie> movies = movieService.getMovies();

        model.addAttribute("movies", movies);

        return "index";
    }

    @PostMapping("/add")
    public String addMovie(@ModelAttribute Movie movie) {

        movieService.addMovie(movie);

        return "redirect:/";
    }

    @GetMapping("/search")
    public String searchMovie(@RequestParam String name, Model model) {

        List<Movie> movies = movieService.searchMovie(name);

        model.addAttribute("movies", movies);

        return "index";
    }

    @PostMapping("/delete")
    public String deleteMovie(@RequestParam String name) {

        movieService.deleteMovie(name);

        return "redirect:/";
    }
}*/