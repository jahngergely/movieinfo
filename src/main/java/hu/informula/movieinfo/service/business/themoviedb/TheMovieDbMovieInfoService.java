package hu.informula.movieinfo.service.business.themoviedb;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

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
    protected int getFirstPageOfMoviesSync(List<Movie> movies, String searchTerm) {
        final TheMovieDbSearchResponse theMovieDbSearchResponse = theMovieDbRestProcessor.getFirstPageSync(searchTerm);

        collectMoviesFromSearchResponse(movies, theMovieDbSearchResponse);

        return theMovieDbSearchResponse.getTotalPages();
    }

    @Override
    protected void fetchMoviePagesAsync(List<Movie> movies, String searchTerm, int page) {
        List<Integer> pages = IntStream.rangeClosed(2, page)
                .boxed().collect(Collectors.toList());

        Flux.fromIterable(pages)
                .parallel()
                .runOn(Schedulers.elastic())
                .flatMap((pageToCollect) -> theMovieDbRestProcessor.getPageAsync(searchTerm, pageToCollect))
                .doOnNext((theMovieDbSearchResponse) -> {
                    collectMoviesFromSearchResponse(movies, theMovieDbSearchResponse);
                })
                .sequential()
                .blockLast();
    }

    @Override
    protected void completeMovies(List<Movie> movies) {
        log.debug("Collecting movie completing data for {} movies", movies.size());

        Flux.fromIterable(movies)
                .parallel()
                .runOn(Schedulers.elastic())
                .flatMap((movie) -> theMovieDbRestProcessor.getCredits(movie.getId()))
                .doOnNext((theMovieDbCreditsResponse) -> {
                    Optional<Movie> movieToComplete = movies.stream().filter((movie) -> movie.getId().equals(theMovieDbCreditsResponse.getId())).findAny();

                    if (movieToComplete.isPresent())
                        movieToComplete.get().setDirector(theMovieDbCreditsResponse
                                .getCrew().stream().filter((theMovieDbCreditItemResponse) -> Objects
                                        .equals(theMovieDbCreditItemResponse.getJob(), "Director"))
                                .map(TheMovieDbCreditItemResponse::getName).collect(Collectors.toList()));
                })
                .sequential()
                .blockLast();

        log.debug("All movie completing data received");
    }

    private void collectMoviesFromSearchResponse(List<Movie> movies, TheMovieDbSearchResponse theMovieDbSearchResponse) {

        movies.addAll(theMovieDbSearchResponse.getResults().stream().map((theMovieDbSearchItemResponse) -> {
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
    }
}
