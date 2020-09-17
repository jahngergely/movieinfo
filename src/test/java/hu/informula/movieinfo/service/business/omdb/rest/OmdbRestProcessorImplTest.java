package hu.informula.movieinfo.service.business.omdb.rest;

import hu.informula.movieinfo.service.business.omdb.pojo.OmdbDetailsResponse;
import hu.informula.movieinfo.service.business.omdb.pojo.OmdbSearchResponse;
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

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.matchers.Times.exactly;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(PER_CLASS)
class OmdbRestProcessorImplTest {

    private final String API_BASE_URL = "http://localhost:1080";
    private final String API_KEY = "apikey";
    private final String SEARCH_TERM = "Matrix";
    private final String MOVIE_ID = "tt0133093";
    private final int PAGE = 1;

    private ClientAndServer mockServer;

    @Autowired
    private OmdbRestProcessorImpl omdbRestProcessorImpl;

    @BeforeAll
    public void startServer() {
        ReflectionTestUtils.setField(omdbRestProcessorImpl, "apiKey", API_KEY);
        ReflectionTestUtils.setField(omdbRestProcessorImpl, "apiBaseUrl", API_BASE_URL);
        mockServer = startClientAndServer(1080);
    }

    @Test
    public void test_getMovies() {
        mockServer.when(request().withMethod("GET").withPath("/").withQueryStringParameter("s", SEARCH_TERM), exactly(1))
                .respond(response().withStatusCode(200).withHeaders(
                        new Header("Content-Type", "application/json; charset=utf-8"),
                        new Header("Cache-Control", "public, max-age=86400"))
                        .withBody(omdbSearchresponseBody));
        OmdbSearchResponse omdbSearchResponse = omdbRestProcessorImpl.getMovies(SEARCH_TERM, PAGE);
        assertEquals(1, omdbSearchResponse.getTotalResults());
        assertEquals(MOVIE_ID, omdbSearchResponse.getSearch().get(0).getImdbId());
        assertEquals("The Matrix", omdbSearchResponse.getSearch().get(0).getTitle());
    }

    @Test
    public void test_getDetails() {
        mockServer.when(request().withMethod("GET").withPath("/").withQueryStringParameter("i", MOVIE_ID), exactly(1))
                .respond(response().withStatusCode(200).withHeaders(
                        new Header("Content-Type", "application/json; charset=utf-8"),
                        new Header("Cache-Control", "public, max-age=86400"))
                        .withBody(omdbDetailsResponseBody));
        OmdbDetailsResponse omdbDetailsResponse = omdbRestProcessorImpl.getDetails(MOVIE_ID);
        assertEquals("Lana Wachowski, Lilly Wachowski", omdbDetailsResponse.getDirector());
        assertEquals("1999", omdbDetailsResponse.getYear());
        assertEquals("The Matrix", omdbDetailsResponse.getTitle());
    }

    @AfterAll
    public void stopServer() {
        mockServer.stop();
    }


    private final String omdbSearchresponseBody = "{\n" +
            "\"Search\": [{\n" +
            "\"Title\": \"The Matrix\",\n" +
            "\"Year\": \"1999\",\n" +
            "\"imdbID\": \"tt0133093\",\n" +
            "\"Type\": \"movie\",\n" +
            "\"Poster\": \"https://m.media-amazon.com/images/M/MV5BNzQzOTk3OTAtNDQ0Zi00ZTVkLWI0MTEtMDllZjNkYzNjNTc4L2ltYWdlXkEyXkFqcGdeQXVyNjU0OTQ0OTY@._V1_SX300.jpg\"\n" +
            "}],\n" +
            "\"totalResults\": \"1\",\n" +
            "\"Response\": \"True\"\n" +
            "}";

    private final String omdbDetailsResponseBody = "{\n" +
            "\"Title\": \"The Matrix\",\n" +
            "\"Year\": \"1999\",\n" +
            "\"Rated\": \"R\",\n" +
            "\"Released\": \"31 Mar 1999\",\n" +
            "\"Runtime\": \"136 min\",\n" +
            "\"Genre\": \"Action, Sci-Fi\",\n" +
            "\"Director\": \"Lana Wachowski, Lilly Wachowski\",\n" +
            "\"Writer\": \"Lilly Wachowski, Lana Wachowski\",\n" +
            "\"Actors\": \"Keanu Reeves, Laurence Fishburne, Carrie-Anne Moss, Hugo Weaving\",\n" +
            "\"Plot\": \"A computer hacker learns from mysterious rebels about the true nature of his reality and his role in the war against its controllers.\",\n" +
            "\"Language\": \"English\",\n" +
            "\"Country\": \"USA\",\n" +
            "\"Awards\": \"Won 4 Oscars. Another 37 wins & 51 nominations.\",\n" +
            "\"Poster\": \"https://m.media-amazon.com/images/M/MV5BNzQzOTk3OTAtNDQ0Zi00ZTVkLWI0MTEtMDllZjNkYzNjNTc4L2ltYWdlXkEyXkFqcGdeQXVyNjU0OTQ0OTY@._V1_SX300.jpg\",\n" +
            "\"Ratings\": [{\n" +
            "\"Source\": \"Internet Movie Database\",\n" +
            "\"Value\": \"8.7/10\"\n" +
            "}, {\n" +
            "\"Source\": \"Rotten Tomatoes\",\n" +
            "\"Value\": \"88%\"\n" +
            "}, {\n" +
            "\"Source\": \"Metacritic\",\n" +
            "\"Value\": \"73/100\"\n" +
            "}],\n" +
            "\"Metascore\": \"73\",\n" +
            "\"imdbRating\": \"8.7\",\n" +
            "\"imdbVotes\": \"1,631,904\",\n" +
            "\"imdbID\": \"tt0133093\",\n" +
            "\"Type\": \"movie\",\n" +
            "\"DVD\": \"21 Sep 1999\",\n" +
            "\"BoxOffice\": \"N/A\",\n" +
            "\"Production\": \"Warner Bros. Pictures\",\n" +
            "\"Website\": \"N/A\",\n" +
            "\"Response\": \"True\"\n" +
            "}";
}