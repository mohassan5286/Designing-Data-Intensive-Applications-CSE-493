package com.example.movieinfoservice.resources;

import com.example.movieinfoservice.Repository.MovieRepository;
import com.example.movieinfoservice.models.Movie;
import com.example.movieinfoservice.models.MovieSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/movies")
public class MovieResource {

    @Value("${api.key}")
    private String apiKey;

    private RestTemplate restTemplate;

    @Autowired
    private MovieRepository movieRepository;

    public MovieResource(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @RequestMapping("/{movieId}")
    public Movie getMovieInfo(@PathVariable("movieId") String movieId) {

        Movie cachedMovie = movieRepository.findByMovieId(movieId);

        if(cachedMovie != null) return cachedMovie;

        // Get the movie info from TMDB
        final String url = "https://api.themoviedb.org/3/movie/" + movieId + "?api_key=" + apiKey;
        MovieSummary movieSummary = restTemplate.getForObject(url, MovieSummary.class);
        Movie resultMovie =  new Movie(movieId, movieSummary.getTitle(), movieSummary.getOverview());
        movieRepository.save(resultMovie);
        return new Movie(movieId, movieSummary.getTitle(), movieSummary.getOverview());
    }
}