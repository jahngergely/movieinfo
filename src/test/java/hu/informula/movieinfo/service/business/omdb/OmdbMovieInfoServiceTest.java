package hu.informula.movieinfo.service.business.omdb;

import hu.informula.movieinfo.service.business.omdb.pojo.OmdbDetailsResponse;
import hu.informula.movieinfo.service.pojo.Movie;
import hu.informula.movieinfo.service.business.omdb.pojo.OmdbSearchItemResponse;
import hu.informula.movieinfo.service.business.omdb.pojo.OmdbSearchResponse;
import hu.informula.movieinfo.service.business.omdb.rest.OmdbRestProcessor;
import hu.informula.movieinfo.utils.ApiType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpStatusCodeException;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class OmdbMovieInfoServiceTest {
    private final String MOVIE_1_ID = "1";
    private final String MOVIE_2_ID = "2";
    private final String MOVIE_TITLE = "Dennis the Menace";
    private final String DIRECTOR_NAME = "Nick Castle";
    private final String YEAR = "1993";
    private final String ERROR = "Some Error";
    private final String ERROR_EMPTY = "Movie not found!";

    @Autowired
    private OmdbMovieInfoService omdbMovieInfoService;

    @MockBean
    private OmdbRestProcessor omdbRestProcessor;

    @Test
    public void test_getMoviesListButOnlyMovies() {
        when(omdbRestProcessor.getFirstPageSync(isA(String.class))).thenReturn(getSearchResponse(MOVIE_1_ID, 1, false, false));
        when(omdbRestProcessor.getDetails(isA(String.class))).thenReturn(Mono.just(1).map(number -> getDetailsResponse(MOVIE_1_ID, false)));

        assertEquals(getExpectedMovieList(), omdbMovieInfoService.getMovieList("Dennis"));
    }

    @Test
    public void test_getMoviesListMultiplePages() {
        ReflectionTestUtils.setField(omdbMovieInfoService, "pageSize", 1);
        when(omdbRestProcessor.getFirstPageSync(isA(String.class))).thenReturn(getSearchResponse(MOVIE_1_ID, 2, false, false));
        when(omdbRestProcessor.getPageAsync(isA(String.class), isA(Integer.class))).thenReturn(Mono.just(1).map(number -> getSearchResponse(MOVIE_2_ID, 2, false, false)));
        when(omdbRestProcessor.getDetails(isA(String.class)))
                .thenReturn(Mono.just(1).map(number -> getDetailsResponse(MOVIE_1_ID, false)))
                .thenReturn(Mono.just(1).map(number -> getDetailsResponse(MOVIE_2_ID, false)));

        assertEquals(getExpectedMovieListMultiplePages(), omdbMovieInfoService.getMovieList("Dennis"));
    }

    @Test
    public void test_getMoviesListFaultySearch() {
        when(omdbRestProcessor.getFirstPageSync(isA(String.class))).thenReturn(getSearchResponse(MOVIE_1_ID, 1, true, false));

        HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class, () -> {
            omdbMovieInfoService.getMovieList("Dennis");
        });

        assertEquals(ERROR, exception.getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
    }

    @Test
    public void test_getMoviesListEmptySearch() {
        when(omdbRestProcessor.getFirstPageSync(isA(String.class))).thenReturn(getSearchResponse(MOVIE_1_ID, 1, true, true));
        assertEquals(Collections.emptyList(), omdbMovieInfoService.getMovieList("Dennis"));
    }


    @Test
    public void test_getMovieDetailsFaultySearch() {
        when(omdbRestProcessor.getFirstPageSync(isA(String.class))).thenReturn(getSearchResponse(MOVIE_1_ID, 1, false, false));
        when(omdbRestProcessor.getDetails(isA(String.class))).thenReturn(Mono.just(1).map(number -> getDetailsResponse(MOVIE_1_ID, true)));

        HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class, () -> {
            omdbMovieInfoService.getMovieList("Dennis");
        });

        assertEquals(ERROR, exception.getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
    }

    @Test
    public void test_hasApiOmdb() {
        assertTrue(omdbMovieInfoService.hasApiType(ApiType.OMDB));
    }


    private OmdbSearchResponse getSearchResponse(String id, int totalResult, boolean faulty, boolean empty) {
        OmdbSearchResponse mockSearchResponse = new OmdbSearchResponse();
        mockSearchResponse.setTotalResults(totalResult);
        mockSearchResponse.setResponse(!faulty);

        if (faulty)
            mockSearchResponse.setError(empty ? ERROR_EMPTY : ERROR);

        OmdbSearchItemResponse mockItemResponseMovie = new OmdbSearchItemResponse();
        mockItemResponseMovie.setImdbId(id);
        mockItemResponseMovie.setTitle(MOVIE_TITLE);
        mockItemResponseMovie.setType("movie");

        OmdbSearchItemResponse mockItemResponseGame = new OmdbSearchItemResponse();
        mockItemResponseGame.setImdbId(id);
        mockItemResponseGame.setTitle(MOVIE_TITLE);
        mockItemResponseGame.setType("game");
        mockSearchResponse.setSearch(Arrays.asList(mockItemResponseMovie, mockItemResponseGame));

        return mockSearchResponse;
    }

    private OmdbDetailsResponse getDetailsResponse(String id, boolean faulty) {
        OmdbDetailsResponse mockDetailsResponse = new OmdbDetailsResponse();
        mockDetailsResponse.setDirector(DIRECTOR_NAME);
        mockDetailsResponse.setImdbId(id);
        mockDetailsResponse.setTitle(MOVIE_TITLE);
        mockDetailsResponse.setYear(YEAR);
        mockDetailsResponse.setResponse(!faulty);

        if (faulty) {
            mockDetailsResponse.setError(ERROR);
        }

        return mockDetailsResponse;
    }

    private List<Movie> getExpectedMovieList() {
        Movie movie = new Movie();
        movie.setId(MOVIE_1_ID);
        movie.setDirector(Collections.singletonList(DIRECTOR_NAME));
        movie.setTitle(MOVIE_TITLE);
        movie.setYear(YEAR);
        return Collections.singletonList(movie);
    }

    private List<Movie> getExpectedMovieListMultiplePages() {
        Movie movie1 = new Movie();
        movie1.setId(MOVIE_1_ID);
        movie1.setDirector(Collections.singletonList(DIRECTOR_NAME));
        movie1.setTitle(MOVIE_TITLE);
        movie1.setYear(YEAR);

        Movie movie2 = new Movie();
        movie2.setId(MOVIE_2_ID);
        movie2.setDirector(Collections.singletonList(DIRECTOR_NAME));
        movie2.setTitle(MOVIE_TITLE);
        movie2.setYear(YEAR);

        return Arrays.asList(movie1, movie2);
    }
}