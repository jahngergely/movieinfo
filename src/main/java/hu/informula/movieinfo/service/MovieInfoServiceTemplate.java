package hu.informula.movieinfo.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import hu.informula.movieinfo.service.pojo.Movie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Flux;

@Slf4j
abstract public class MovieInfoServiceTemplate implements MovieInfoService {

    @Value("${movieinfo.pagesThreshold:0)}")
    private int pagesThreshold;

    @Override
    public List<Movie> getMovieList(String searchTerm) {
        List<Movie> movies = collectMoviesBasedOnSearchString(searchTerm);
        completeMovies(movies);

        return movies;
    }

    protected List<Movie> collectMoviesBasedOnSearchString(String searchTerm) {
        final List<Movie> movies = Collections.synchronizedList(new ArrayList<>());

        int numberOfPages = getFirstPageOfMoviesSync(movies, searchTerm);
        int numberOfPagesWithThreshold = numberOfPages > pagesThreshold && pagesThreshold != 0 ? pagesThreshold : numberOfPages;

        if (numberOfPagesWithThreshold > 1)
            fetchMoviePagesAsync(movies, searchTerm, numberOfPagesWithThreshold);

        return movies;
    }

    abstract protected int getFirstPageOfMoviesSync(List<Movie> movies, String searchTerm);
    abstract protected void fetchMoviePagesAsync(List<Movie> movies, String searchTerm, int numberOfPages);
    abstract protected void completeMovies(List<Movie> movies);
}
