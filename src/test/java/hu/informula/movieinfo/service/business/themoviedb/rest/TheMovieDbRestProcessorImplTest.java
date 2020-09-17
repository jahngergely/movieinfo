package hu.informula.movieinfo.service.business.themoviedb.rest;

import static org.junit.jupiter.api.Assertions.*;

import hu.informula.movieinfo.service.business.themoviedb.pojo.TheMovieDbCreditsResponse;
import hu.informula.movieinfo.service.business.themoviedb.pojo.TheMovieDbSearchResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.text.SimpleDateFormat;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.Mockito.when;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.matchers.Times.exactly;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(PER_CLASS)
class TheMovieDbRestProcessorImplTest {
    private final String API_BASE_URL = "http://localhost:1080";
    private final String API_KEY = "apikey";
    private final String SEARCH_TERM = "Avengers";
    private final String MOVIE_ID = "1";
    private final int PAGE = 1;

    private ClientAndServer mockServer;

    @Autowired
    private TheMovieDbRestProcessorImpl theMovieDbRestProcessorImpl;

    @BeforeAll
    public void startServer() {
        ReflectionTestUtils.setField(theMovieDbRestProcessorImpl, "apiKey", API_KEY);
        ReflectionTestUtils.setField(theMovieDbRestProcessorImpl, "apiBaseUrl", API_BASE_URL);
        mockServer = startClientAndServer(1080);
    }

    @Test
    public void test_getMovies() {
        mockServer.when(request().withMethod("GET").withPath("/search/movie"), exactly(1))
                .respond(response().withStatusCode(200).withHeaders(
                        new Header("Content-Type", "application/json; charset=utf-8"),
                        new Header("Cache-Control", "public, max-age=86400"))
                        .withBody(theMovieDbSearchresponseBody));
        TheMovieDbSearchResponse theMovieDbSearchResponse = theMovieDbRestProcessorImpl.getMovies(SEARCH_TERM, PAGE);
        assertEquals(1, theMovieDbSearchResponse.getPage());
        assertEquals(1, theMovieDbSearchResponse.getTotalResults());
        assertEquals(1, theMovieDbSearchResponse.getTotalPages());
        assertEquals("24428", theMovieDbSearchResponse.getResults().get(0).getId());
        assertEquals("The Avengers", theMovieDbSearchResponse.getResults().get(0).getTitle());
        assertEquals("2012-04-25", (new SimpleDateFormat("yyyy-MM-dd")).format(theMovieDbSearchResponse.getResults().get(0).getReleaseDate()));
    }

    @Test
    public void test_getDetails() {
        mockServer.when(request().withMethod("GET").withPath("/movie/1/credits"), exactly(1))
                .respond(response().withStatusCode(200).withHeaders(
                        new Header("Content-Type", "application/json; charset=utf-8"),
                        new Header("Cache-Control", "public, max-age=86400"))
                        .withBody(theMovieDbCreditsResponse));
        TheMovieDbCreditsResponse theMovieDbCreditsResponse = theMovieDbRestProcessorImpl.getCredits(MOVIE_ID);
        assertEquals("David Fincher", theMovieDbCreditsResponse.getCrew().get(0).getName());
        assertEquals("Director", theMovieDbCreditsResponse.getCrew().get(0).getJob());
        assertEquals("550", theMovieDbCreditsResponse.getId());
    }


    @AfterAll
    public void stopServer() {
        mockServer.stop();
    }

    private final String theMovieDbSearchresponseBody = "{" +
            "  \"page\": 1," +
            "  \"results\": [" +
            "    {" +
            "      \"poster_path\": \"/cezWGskPY5x7GaglTTRN4Fugfb8.jpg\"," +
            "      \"adult\": false," +
            "      \"overview\": \"When an unexpected enemy emerges and threatens global safety and security, Nick Fury, director of the international peacekeeping agency known as S.H.I.E.L.D., finds himself in need of a team to pull the world back from the brink of disaster. Spanning the globe, a daring recruitment effort begins!\"," +
            "      \"release_date\": \"2012-04-25\"," +
            "      \"genre_ids\": [" +
            "        878," +
            "        28," +
            "        12" +
            "      ]," +
            "      \"id\": 24428," +
            "      \"original_title\": \"The Avengers\"," +
            "      \"original_language\": \"en\"," +
            "      \"title\": \"The Avengers\"," +
            "      \"backdrop_path\": \"/hbn46fQaRmlpBuUrEiFqv0GDL6Y.jpg\"," +
            "      \"popularity\": 7.353212," +
            "      \"vote_count\": 8503," +
            "      \"video\": false," +
            "      \"vote_average\": 7.33" +
            "    }" +
            "  ]," +
            "  \"total_results\": 1," +
            "  \"total_pages\": 1" +
            "}";

    private final String theMovieDbCreditsResponse = "{\n" +
            "  \"id\": 550,\n" +
            "  \"cast\": [\n" +
            "    {\n" +
            "      \"cast_id\": 4,\n" +
            "      \"character\": \"The Narrator\",\n" +
            "      \"credit_id\": \"52fe4250c3a36847f80149f3\",\n" +
            "      \"gender\": 2,\n" +
            "      \"id\": 819,\n" +
            "      \"name\": \"Edward Norton\",\n" +
            "      \"order\": 0,\n" +
            "      \"profile_path\": \"/eIkFHNlfretLS1spAcIoihKUS62.jpg\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"crew\": [\n" +
            "    {\n" +
            "      \"credit_id\": \"52fe4250c3a36847f8014a47\",\n" +
            "      \"department\": \"Directing\",\n" +
            "      \"gender\": 2,\n" +
            "      \"id\": 7467,\n" +
            "      \"job\": \"Director\",\n" +
            "      \"name\": \"David Fincher\",\n" +
            "      \"profile_path\": \"/dcBHejOsKvzVZVozWJAPzYthb8X.jpg\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"credit_id\": \"52fe4250c3a36847f8014a4d\",\n" +
            "      \"department\": \"Writing\",\n" +
            "      \"gender\": 0,\n" +
            "      \"id\": 7468,\n" +
            "      \"job\": \"Novel\",\n" +
            "      \"name\": \"Chuck Palahniuk\",\n" +
            "      \"profile_path\": \"/8nOJDJ6SqwV2h7PjdLBDTvIxXvx.jpg\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";
}