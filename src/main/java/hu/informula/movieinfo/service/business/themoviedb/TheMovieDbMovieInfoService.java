package hu.informula.movieinfo.service.business.themoviedb;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import hu.informula.movieinfo.service.business.themoviedb.rest.TheMovieDbRestProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hu.informula.movieinfo.service.MovieInfoServiceTemplate;
import hu.informula.movieinfo.service.pojo.Movie;
import hu.informula.movieinfo.service.business.themoviedb.pojo.TheMovieDbCreditItemResponse;
import hu.informula.movieinfo.service.business.themoviedb.pojo.TheMovieDbCreditsResponse;
import hu.informula.movieinfo.service.business.themoviedb.pojo.TheMovieDbSearchResponse;
import hu.informula.movieinfo.utils.ApiType;

@Slf4j
@Service
public class TheMovieDbMovieInfoService extends MovieInfoServiceTemplate {

    @Autowired
    private TheMovieDbRestProcessor theMovieDbRestProcessor;

    @Override
    public boolean hasApiType(ApiType apiType) {
        return apiType == ApiType.THEMOVIEDB;
    }

    @Override
    protected boolean fetchMovies(List<Movie> movies, String searchTerm, int page) {

        final TheMovieDbSearchResponse searchResponse = theMovieDbRestProcessor.getMovies(searchTerm, page);

        movies.addAll(searchResponse.getResults().stream().map((theMovieDbSearchItemResponse) -> {
            Movie movie = new Movie();
            movie.setId(theMovieDbSearchItemResponse.getId());
            movie.setTitle(theMovieDbSearchItemResponse.getTitle());
            if (theMovieDbSearchItemResponse.getReleaseDate() != null) {
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(theMovieDbSearchItemResponse.getReleaseDate());
                movie.setYear(String.valueOf(calendar.get(Calendar.YEAR)));
            }
            return movie;
        }).collect(Collectors.toList()));

        return page < searchResponse.getTotalPages();
    }

    @Override
    protected void completeMovie(Movie movie) {
        final TheMovieDbCreditsResponse creditsResponse = theMovieDbRestProcessor.getCredits(movie.getId());
        movie.setDirector(creditsResponse
                .getCrew().stream().filter((theMovieDbCreditItemResponse) -> Objects
                        .equals(theMovieDbCreditItemResponse.getJob(), "Director"))
                .map(TheMovieDbCreditItemResponse::getName).collect(Collectors.toList()));
    }
}
