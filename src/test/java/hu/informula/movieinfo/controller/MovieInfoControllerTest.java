package hu.informula.movieinfo.controller;

import hu.informula.movieinfo.persistence.entity.RequestHistoryEntity;
import hu.informula.movieinfo.persistence.service.RequestHistoryCrudService;
import hu.informula.movieinfo.service.business.omdb.OmdbMovieInfoService;
import hu.informula.movieinfo.service.business.themoviedb.TheMovieDbMovieInfoService;
import hu.informula.movieinfo.service.pojo.Movie;
import hu.informula.movieinfo.utils.ApiType;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MovieInfoControllerTest {
    private final String MOVIE_1_TITLE = "Dennis the Menace";
    private final String MOVIE_2_TITLE = "Angels with filthy souls";
    private final String MOVIE_1_YEAR = "1993";
    private final String MOVIE_1_DIRECTOR = "1993";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TheMovieDbMovieInfoService theMovieDbMovieInfoService;

    @MockBean
    private OmdbMovieInfoService omdbMovieInfoService;

    @MockBean
    private RequestHistoryCrudService requestHistoryCrudService;

    @Test
    public void test_unmappedPath() throws Exception {
        mockMvc.perform(get("/unmapped")).andDo(print()).andExpect(status().isNotFound());
    }

    @Test
    public void test_missingApiType() throws Exception {
        mockMvc.perform(get("/movies/Dennis")).andDo(print()).andExpect(status().isBadRequest());
    }

    @Test
    public void test_invalidApiType() throws Exception {
        mockMvc.perform(get("/movies/Dennis?api=notimpl")).andDo(print()).andExpect(status().isBadRequest());
    }

    @Test
    public void test_notImplementedApiType() throws Exception {
        when(omdbMovieInfoService.hasApiType(ApiType.OMDB)).thenReturn(false);
        mockMvc.perform(get("/movies/Dennis?api=omdb")).andDo(print()).andExpect(status().isInternalServerError());
    }

    @Test
    public void test_emptySearchTerm() throws Exception {
        when(omdbMovieInfoService.hasApiType(ApiType.OMDB)).thenReturn(true);
        mockMvc.perform(get("/movies/?api=omdb")).andDo(print()).andExpect(status().isNotFound());
    }

    @Test
    public void test_validRequestEmptyResult() throws Exception {
        when(omdbMovieInfoService.hasApiType(ApiType.OMDB)).thenReturn(true);
        when(omdbMovieInfoService.getMovieList("Dennis")).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/movies/Dennis?api=omdb"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().json("{\"movies\": []}"));
        verify(requestHistoryCrudService, times(2)).save(isA(RequestHistoryEntity.class));
    }

    @Test
    public void test_validRequest() throws Exception {
        when(omdbMovieInfoService.hasApiType(ApiType.OMDB)).thenReturn(true);
        when(omdbMovieInfoService.getMovieList("Dennis")).thenReturn(getMoviesList());
        mockMvc.perform(get("/movies/Dennis?api=omdb"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().json("{\"movies\": [{\n" +
                        "  \"Title\" : \"Dennis the Menace\",\n" +
                        "  \"Director\" : \"1993\",\n" +
                        "  \"Year\" : \"1993\"\n" +
                        "}, {\n" +
                        "  \"Title\" : \"Angels with filthy souls\"\n" +
                        "}]}"));
        verify(requestHistoryCrudService, times(2)).save(isA(RequestHistoryEntity.class));
    }

    @Test
    public void test_throwsHttpServerException() throws Exception {
        when(omdbMovieInfoService.hasApiType(ApiType.OMDB)).thenReturn(true);
        when(omdbMovieInfoService.getMovieList("Dennis")).thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));
        mockMvc.perform(get("/movies/Dennis?api=omdb"))
                .andDo(print()).andExpect(status().isInternalServerError());
        verify(requestHistoryCrudService, times(1)).save(isA(RequestHistoryEntity.class));
    }

    @Test
    public void test_throwsHttpClientException() throws Exception {
        when(omdbMovieInfoService.hasApiType(ApiType.OMDB)).thenReturn(true);
        when(omdbMovieInfoService.getMovieList("Dennis")).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));
        mockMvc.perform(get("/movies/Dennis?api=omdb"))
                .andDo(print()).andExpect(status().isBadRequest());
        verify(requestHistoryCrudService, times(1)).save(isA(RequestHistoryEntity.class));
    }

    @Test
    public void test_requestHistoryEntity() throws Exception {
        when(omdbMovieInfoService.hasApiType(ApiType.OMDB)).thenReturn(true);
        when(omdbMovieInfoService.getMovieList("Dennis")).thenReturn(Collections.emptyList());
        ArgumentCaptor<RequestHistoryEntity> argument = ArgumentCaptor.forClass(RequestHistoryEntity.class);

        mockMvc.perform(get("/movies/Dennis?api=omdb").header("X-Forwarded-For", "178.0.0.14").header("User-Agent", "007").header("Host", "localhost:8080"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().json("{\"movies\": []}"));
        verify(requestHistoryCrudService, times(2)).save(argument.capture());
        List<RequestHistoryEntity> requestHistoryEntities = argument.getAllValues();
        assertEquals("178.0.0.14", requestHistoryEntities.get(0).getIpAddress());
        assertEquals("localhost:8080", requestHistoryEntities.get(0).getHost());
        assertEquals("007", requestHistoryEntities.get(0).getUserAgent());
        assertEquals("Dennis", requestHistoryEntities.get(0).getSearchTerm());
        assertEquals(ApiType.OMDB.toString(), requestHistoryEntities.get(0).getApi());
    }


    List<Movie> getMoviesList() {
        Movie movie1 = new Movie();
        movie1.setTitle(MOVIE_1_TITLE);
        movie1.setYear(MOVIE_1_YEAR);
        movie1.setDirector(Collections.singletonList(MOVIE_1_DIRECTOR));

        Movie movie2 = new Movie();
        movie2.setTitle(MOVIE_2_TITLE);

        return Arrays.asList(movie1, movie2);
    }
}