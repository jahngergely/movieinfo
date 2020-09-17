package hu.informula.movieinfo.service;

import java.util.ArrayList;
import java.util.List;

import hu.informula.movieinfo.service.pojo.Movie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
abstract public class MovieInfoServiceTemplate implements MovieInfoService {

    @Value("${movieinfo.pagesThreshold:0)}")
    private int pagesThreshold;

    @Override
    public List<Movie> getMovieList(String searchTerm) {
        List<Movie> list = new ArrayList<>();

        List<Movie> movies = collectMoviesBasedOnSearchString(searchTerm);

        for (Movie movie : movies) {
            completeMovie(movie);
            list.add(movie);
        }

        return list;
    }

    protected List<Movie> collectMoviesBasedOnSearchString(String searchTerm) {
        final List<Movie> movies = new ArrayList<>();
        int page = 1;

        while (fetchMovies(movies, searchTerm, page) && (pagesThreshold == 0 ||  page < pagesThreshold)) {
            page++;
        }

        return movies;
    }

    abstract protected boolean fetchMovies(List<Movie> movieIds, String searchTerm, int page);
    abstract protected void completeMovie(Movie movie);
}
