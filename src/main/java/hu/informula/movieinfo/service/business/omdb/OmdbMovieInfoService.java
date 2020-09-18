package hu.informula.movieinfo.service.business.omdb;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import hu.informula.movieinfo.service.business.omdb.rest.OmdbRestProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import hu.informula.movieinfo.service.MovieInfoServiceTemplate;
import hu.informula.movieinfo.service.business.omdb.pojo.OmdbSearchResponse;
import hu.informula.movieinfo.service.pojo.Movie;
import hu.informula.movieinfo.utils.ApiType;
import org.springframework.web.client.HttpServerErrorException;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
public class OmdbMovieInfoService extends MovieInfoServiceTemplate {

    @Value("${omdb.pageSize:10)}")
    private int pageSize;

    @Autowired
    protected OmdbRestProcessor omdbRestProcessor;

    @Override
    public boolean hasApiType(ApiType apiType) {
        return apiType == ApiType.OMDB;
    }


    @Override
    protected int getFirstPageOfMoviesSync(List<Movie> movies, String searchTerm) {
        final OmdbSearchResponse omdbSearchResponse = omdbRestProcessor.getFirstPageSync(searchTerm);

        collectMoviesFromSearchResponse(movies, omdbSearchResponse);

        return (int) Math.ceil(omdbSearchResponse.getTotalResults() / (double)pageSize);
    }

    @Override
    protected void fetchMoviePagesAsync(List<Movie> movies, String searchTerm, int page) {
        List<Integer> pages = IntStream.rangeClosed(2, page)
                .boxed().collect(Collectors.toList());

        Flux.fromIterable(pages)
                .parallel()
                .runOn(Schedulers.elastic())
                .flatMap((pageToCollect) -> omdbRestProcessor.getPageAsync(searchTerm, pageToCollect))
                .doOnNext((omdbSearchResponse) -> {
                    collectMoviesFromSearchResponse(movies, omdbSearchResponse);
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
                .flatMap((movie) -> omdbRestProcessor.getDetails(movie.getId()))
                .doOnNext((omdbDetailsResponse) -> {
                    if (!omdbDetailsResponse.isResponse())
                        throw new HttpServerErrorException(omdbDetailsResponse.getError(), HttpStatus.INTERNAL_SERVER_ERROR, "", null, null, null);

                    Optional<Movie> movieToComplete = movies.stream().filter((movie) -> movie.getId().equals(omdbDetailsResponse.getImdbId())).findAny();

                    if (movieToComplete.isPresent()) {
                        movieToComplete.get().setYear(omdbDetailsResponse.getYear());
                        movieToComplete.get().setDirector(Arrays.asList(omdbDetailsResponse.getDirector().split(",")));
                        log.debug("Completed movie: {}", movieToComplete.get());
                    }

                })
                .sequential()
                .blockLast();

        log.debug("All movie completing data received");
    }

    private void collectMoviesFromSearchResponse(List<Movie> movies, OmdbSearchResponse omdbSearchResponse) {
        if (!omdbSearchResponse.isResponse()) {
            // This can happen in case of empty result set or end of paging(should not occure, maybe if omdb changes the
            // pageSize)
            if (!Objects.equals(omdbSearchResponse.getError(), "Movie not found!"))
                throw new HttpServerErrorException(omdbSearchResponse.getError(), HttpStatus.INTERNAL_SERVER_ERROR, "", null, null, null);
        } else {
            movies.addAll(omdbSearchResponse.getSearch().stream().filter((omdbSearchItemResponse) -> omdbSearchItemResponse.getType().equals("movie")).map((omdbSearchItemResponse) -> {
                Movie movie = new Movie();
                movie.setId(omdbSearchItemResponse.getImdbId());
                movie.setTitle(omdbSearchItemResponse.getTitle());
                return movie;
            }).collect(Collectors.toList()));
        }
    }
}
