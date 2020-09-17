package hu.informula.movieinfo.service.business.themoviedb.rest;

import hu.informula.movieinfo.service.business.themoviedb.pojo.TheMovieDbCreditsResponse;
import hu.informula.movieinfo.service.business.themoviedb.pojo.TheMovieDbSearchResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Slf4j
@Service
public class TheMovieDbRestProcessorImpl implements TheMovieDbRestProcessor {

    @Value("${themoviedb.apiKey:invalid)}")
    private String apiKey;

    @Value("${themoviedb.apiBaseUrl:http://api.themoviedb.org/3)}")
    private String apiBaseUrl;

    private RestTemplate restTemplate = new RestTemplate();

    @Override
    @Cacheable("theMovieDbSearch")
    public TheMovieDbSearchResponse getMovies(String searchTerm, int page) {
        final String url = String.format("%s/search/movie?query={searchTerm}&api_key={apiKey}&include_adult=true&page={page}", apiBaseUrl);

        log.debug("Query url: {}, searchTerm: {}, apiKey: {}, page: {}", url, searchTerm, apiKey, page);

        TheMovieDbSearchResponse theMovieDbSearchResponse = restTemplate.getForObject(url, TheMovieDbSearchResponse.class, searchTerm, apiKey, page);

        log.debug("REST API response object: {}", theMovieDbSearchResponse.toString());

        return theMovieDbSearchResponse;
    }

    @Override
    @Cacheable("theMovieDbCredits")
    public TheMovieDbCreditsResponse getCredits(String id) {
        final String url = String.format("%s/movie/{id}/credits?api_key={apiKey}", apiBaseUrl);

        log.debug("Query url: {}, id: {}, apiKey: {}", url, id, apiKey);

        TheMovieDbCreditsResponse theMovieDbCreditsResponse = restTemplate.getForObject(url, TheMovieDbCreditsResponse.class, id, apiKey);

        log.debug("REST API response object: {}", theMovieDbCreditsResponse.toString());

        return theMovieDbCreditsResponse;
    }
}
