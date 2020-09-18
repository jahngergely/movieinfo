package hu.informula.movieinfo.service.business.themoviedb;

import hu.informula.movieinfo.service.pojo.Movie;
import hu.informula.movieinfo.service.business.themoviedb.pojo.TheMovieDbCreditItemResponse;
import hu.informula.movieinfo.service.business.themoviedb.pojo.TheMovieDbCreditsResponse;
import hu.informula.movieinfo.service.business.themoviedb.pojo.TheMovieDbSearchItemResponse;
import hu.informula.movieinfo.service.business.themoviedb.pojo.TheMovieDbSearchResponse;
import hu.informula.movieinfo.service.business.themoviedb.rest.TheMovieDbRestProcessor;
import hu.informula.movieinfo.utils.ApiType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class TheMovieDbMovieInfoServiceTest {
    private final String MOVIE_1_ID = "1";
    private final String MOVIE_2_ID = "2";
    private final String MOVIE_3_ID = "3";
    private final String MOVIE_TITLE = "Dennis the Menace";
    private final String DIRECTOR_JOB = "Director";
    private final String DIRECTOR_NAME = "Nick Castle";
    private final String OTHER_JOB = "Victim";
    private final String OTHER_NAME = "Mr. Wilson";

    @Autowired
    private TheMovieDbMovieInfoService theMovieDbMovieInfoService;

    @MockBean
    private TheMovieDbRestProcessor theMovieDbRestProcessor;

    @Test
    public void test_getMoviesList() {
        when(theMovieDbRestProcessor.getFirstPageSync(isA(String.class))).thenReturn(getSearchResponse(MOVIE_1_ID, 1, 1, 1));
        when(theMovieDbRestProcessor.getCredits(isA(String.class))).thenReturn(Mono.just(1).map(number -> getCreditsResponse(MOVIE_1_ID, true)));

        assertEquals(getExpectedMovieList(), theMovieDbMovieInfoService.getMovieList("Dennis"));
    }

    @Test
    public void test_getMoviesListEmptyFields() {
        when(theMovieDbRestProcessor.getFirstPageSync(isA(String.class))).thenReturn(getSearchResponseEmptyFields());
        when(theMovieDbRestProcessor.getCredits(isA(String.class))).thenReturn(Mono.just(1).map(number -> getCreditsResponse(MOVIE_1_ID, false)));

        assertEquals(getExpectedMovieListEmptyFields(), theMovieDbMovieInfoService.getMovieList("Dennis"));
    }

    @Test
    public void test_getMoviesListWithPages() {
        when(theMovieDbRestProcessor.getFirstPageSync(isA(String.class))).thenReturn(getSearchResponse(MOVIE_2_ID,1, 2, 2));
        when(theMovieDbRestProcessor.getPageAsync(isA(String.class), isA(Integer.class))).thenReturn(Mono.just(1).map(number -> getSearchResponse(MOVIE_2_ID, 2, 2, 2)));
        when(theMovieDbRestProcessor.getCredits(isA(String.class)))
                .thenReturn(Mono.just(1).map(number -> getCreditsResponse(MOVIE_1_ID, true)))
                .thenReturn(Mono.just(1).map(number -> getCreditsResponse(MOVIE_2_ID, true)));

        assertEquals(2, theMovieDbMovieInfoService.getMovieList("Dennis").size());
    }

    @Test
    public void test_getMoviesListWithPagesExceedsThreshold() {
        ReflectionTestUtils.setField(theMovieDbMovieInfoService, "pagesThreshold", 2);
        when(theMovieDbRestProcessor.getFirstPageSync(isA(String.class))).thenReturn(getSearchResponse(MOVIE_2_ID,1, 3, 3));
        when(theMovieDbRestProcessor.getPageAsync(isA(String.class), isA(Integer.class))).thenReturn(Mono.just(1).map(number -> getSearchResponse(MOVIE_2_ID, 2, 3, 3)))
                .thenReturn(Mono.just(1).map(number -> getSearchResponse(MOVIE_3_ID, 3, 3, 3)));
        when(theMovieDbRestProcessor.getCredits(isA(String.class)))
                .thenReturn(Mono.just(1).map(number -> getCreditsResponse(MOVIE_1_ID, true)))
                .thenReturn(Mono.just(1).map(number -> getCreditsResponse(MOVIE_2_ID, true)))
                .thenReturn(Mono.just(1).map(number -> getCreditsResponse(MOVIE_3_ID, true)));

        assertEquals(2, theMovieDbMovieInfoService.getMovieList("Dennis").size());
    }

    @Test
    public void test_hasApiTheMovieDb() {
        assertTrue(theMovieDbMovieInfoService.hasApiType(ApiType.THEMOVIEDB));
    }

    private TheMovieDbSearchResponse getSearchResponse(String id, int page, int totalPage, int totalResult) {
        TheMovieDbSearchResponse mockSearchResponse = new TheMovieDbSearchResponse();
        mockSearchResponse.setPage(page);
        mockSearchResponse.setTotalPages(totalPage);
        mockSearchResponse.setTotalResults(totalResult);
        TheMovieDbSearchItemResponse mockItemResponse = new TheMovieDbSearchItemResponse();
        mockItemResponse.setId(id);
        Calendar calendar = Calendar.getInstance();
        calendar.set(1993, 06, 25);
        Date releaseDate = calendar.getTime();
        mockItemResponse.setReleaseDate(releaseDate);
        mockItemResponse.setTitle(MOVIE_TITLE);
        mockSearchResponse.setResults(Collections.singletonList(mockItemResponse));

        return mockSearchResponse;
    }

    private TheMovieDbSearchResponse getSearchResponseEmptyFields() {
        TheMovieDbSearchResponse mockSearchResponse = new TheMovieDbSearchResponse();
        mockSearchResponse.setPage(1);
        mockSearchResponse.setTotalPages(1);
        mockSearchResponse.setTotalResults(1);
        TheMovieDbSearchItemResponse mockItemResponse = new TheMovieDbSearchItemResponse();
        mockItemResponse.setId(MOVIE_1_ID);
        mockItemResponse.setTitle(MOVIE_TITLE);
        mockSearchResponse.setResults(Collections.singletonList(mockItemResponse));

        return mockSearchResponse;
    }

    private TheMovieDbCreditsResponse getCreditsResponse(String id, boolean hasDirector) {
        TheMovieDbCreditsResponse mockCreditsResponse = new TheMovieDbCreditsResponse();
        mockCreditsResponse.setId(id);

        List<TheMovieDbCreditItemResponse> creditItemResponses = new ArrayList<>();
        if (hasDirector) {
            TheMovieDbCreditItemResponse mockItemResponseDirector = new TheMovieDbCreditItemResponse();
            mockItemResponseDirector.setJob(DIRECTOR_JOB);
            mockItemResponseDirector.setName(DIRECTOR_NAME);
            creditItemResponses.add(mockItemResponseDirector);
        }

        TheMovieDbCreditItemResponse mockItemResponseOther = new TheMovieDbCreditItemResponse();
        mockItemResponseOther.setJob(OTHER_JOB);
        mockItemResponseOther.setName(OTHER_NAME);
        creditItemResponses.add(mockItemResponseOther);

        mockCreditsResponse.setCrew(creditItemResponses);
        return mockCreditsResponse;
    }

    private List<Movie> getExpectedMovieList() {
        Movie movie = new Movie();
        movie.setId(MOVIE_1_ID);
        movie.setDirector(Collections.singletonList(DIRECTOR_NAME));
        movie.setTitle(MOVIE_TITLE);
        movie.setYear("1993");
        return Collections.singletonList(movie);
    }

    private List<Movie> getExpectedMovieListEmptyFields() {
        Movie movie = new Movie();
        movie.setId(MOVIE_1_ID);
        movie.setDirector(Collections.emptyList());
        movie.setTitle(MOVIE_TITLE);
        return Collections.singletonList(movie);
    }
}