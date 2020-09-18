package hu.informula.movieinfo.service.business.omdb.rest;

import hu.informula.movieinfo.service.business.omdb.pojo.OmdbDetailsResponse;
import hu.informula.movieinfo.service.business.omdb.pojo.OmdbSearchResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class OmdbRestProcessorImpl implements OmdbRestProcessor {

    @Value("${omdb.apiKey:invalid)}")
    private String apiKey;

    @Value("${omdb.apiBaseUrl:http://www.omdbapi.com)}")
    private String apiBaseUrl;

    @Override
    public OmdbSearchResponse getFirstPageSync(String searchTerm) {
        log.debug("Collecting first page synchronously with search term: {}", searchTerm);
        WebClient webClient = WebClient.create(apiBaseUrl);
        return webClient.get()
                .uri("/?s={searchTerm}&apiKey={apiKey}&page={page}", searchTerm, apiKey, 1)
                .retrieve()
                .toEntity(OmdbSearchResponse.class)
                .block()
                .getBody();
    }

    @Override
    public Mono<OmdbSearchResponse> getPageAsync(String searchTerm, int page) {
        log.debug("Collecting page {} asynchronously with search term: {}", page, searchTerm);
        WebClient webClient = WebClient.create(apiBaseUrl);
        return webClient.get()
                .uri("/?s={searchTerm}&apiKey={apiKey}&page={page}", searchTerm, apiKey, page)
                .retrieve()
                .bodyToMono(OmdbSearchResponse.class);
    }

    @Override
    public Mono<OmdbDetailsResponse> getDetails(String id) {
        log.debug("Completing movie: {}", id);
        WebClient webClient = WebClient.create(apiBaseUrl);
        return webClient.get()
                .uri("/?i={id}&apiKey={apiKey}", id, apiKey)
                .retrieve()
                .bodyToMono(OmdbDetailsResponse.class);
    }
}
