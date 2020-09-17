package hu.informula.movieinfo.service.business.omdb;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import hu.informula.movieinfo.service.business.omdb.rest.OmdbRestProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import hu.informula.movieinfo.service.MovieInfoServiceTemplate;
import hu.informula.movieinfo.service.business.omdb.pojo.OmdbDetailsResponse;
import hu.informula.movieinfo.service.business.omdb.pojo.OmdbSearchResponse;
import hu.informula.movieinfo.service.pojo.Movie;
import hu.informula.movieinfo.utils.ApiType;
import org.springframework.web.client.HttpServerErrorException;

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
    protected boolean fetchMovies(List<Movie> movies, String searchTerm, int page) {
        final OmdbSearchResponse searchResponse = omdbRestProcessor.getMovies(searchTerm, page);

        if (!searchResponse.isResponse()) {
            // This can happen in case of empty result set or end of paging(should not occure, maybe if omdb changes the
            // pageSize)
            if (Objects.equals(searchResponse.getError(), "Movie not found!")) {
                return false;
            }

            throw new HttpServerErrorException(searchResponse.getError(), HttpStatus.INTERNAL_SERVER_ERROR, "", null, null, null);
        }

        movies.addAll(searchResponse.getSearch().stream().filter((omdbSearchItemResponse) -> omdbSearchItemResponse.getType().equals("movie")).map((omdbSearchItemResponse) -> {
            Movie movie = new Movie();
            movie.setId(omdbSearchItemResponse.getImdbId());
            movie.setTitle(omdbSearchItemResponse.getTitle());
            return movie;
        }).collect(Collectors.toList()));

        return page * pageSize < searchResponse.getTotalResults();
    }

    @Override
    protected void completeMovie(Movie movie) {
        final OmdbDetailsResponse detailsResponse = omdbRestProcessor.getDetails(movie.getId());

        if (!detailsResponse.isResponse())
            throw new HttpServerErrorException(detailsResponse.getError(), HttpStatus.INTERNAL_SERVER_ERROR, "", null, null, null);

        movie.setYear(detailsResponse.getYear());
        movie.setDirector(Arrays.asList(detailsResponse.getDirector().split(",")));
    }
}
