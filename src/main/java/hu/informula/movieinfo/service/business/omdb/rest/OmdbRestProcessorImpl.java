package hu.informula.movieinfo.service.business.omdb.rest;

import hu.informula.movieinfo.service.business.omdb.pojo.OmdbDetailsResponse;
import hu.informula.movieinfo.service.business.omdb.pojo.OmdbSearchResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class OmdbRestProcessorImpl implements OmdbRestProcessor {

    @Value("${omdb.apiKey:invalid)}")
    private String apiKey;

    @Value("${omdb.apiBaseUrl:http://www.omdbapi.com)}")
    private String apiBaseUrl;

    private RestTemplate restTemplate = new RestTemplate();

    @Override
    @Cacheable("omdbSearch")
    public OmdbSearchResponse getMovies(String searchTerm, int page) {
        final String url = String.format("%s/?s={searchTerm}&apiKey={apiKey}&page={page}", apiBaseUrl);
        log.debug("Query url: {}, searchTerm: {}, apiKey: {}, page: {}", url, searchTerm, apiKey, page);

        OmdbSearchResponse omdbSearchResponse = restTemplate.getForObject(url, OmdbSearchResponse.class, searchTerm, apiKey, page);

        log.debug("REST API response object: {}", omdbSearchResponse.toString());

        return omdbSearchResponse;
    }

    @Override
    @Cacheable("omdbDetails")
    public OmdbDetailsResponse getDetails(String id) {
        final String url = String.format("%s/?i={id}&apiKey={apiKey}", apiBaseUrl);

        log.debug("Query url: {}, id: {}, apiKey: {}", url, id, apiKey);

        OmdbDetailsResponse omdbDetailsResponse = restTemplate.getForObject(url, OmdbDetailsResponse.class, id, apiKey);

        log.debug("REST API response object: {}", omdbDetailsResponse.toString());

        return omdbDetailsResponse;
    }
}
