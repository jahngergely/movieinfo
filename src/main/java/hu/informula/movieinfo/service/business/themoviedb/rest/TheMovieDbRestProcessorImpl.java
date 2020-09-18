package hu.informula.movieinfo.service.business.themoviedb.rest;

import hu.informula.movieinfo.service.business.themoviedb.pojo.TheMovieDbCreditsResponse;
import hu.informula.movieinfo.service.business.themoviedb.pojo.TheMovieDbSearchResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Slf4j
@Service
public class TheMovieDbRestProcessorImpl implements TheMovieDbRestProcessor {

    @Value("${themoviedb.apiKey:invalid)}")
    private String apiKey;

    @Value("${themoviedb.apiBaseUrl:http://api.themoviedb.org/3)}")
    private String apiBaseUrl;

    @Override
    public TheMovieDbSearchResponse getFirstPageSync(String searchTerm) {
        log.debug("Collecting first page synchronously with search term: {}", searchTerm);
        WebClient webClient = WebClient.create(apiBaseUrl);
        return webClient.get()
                .uri("/search/movie?query={searchTerm}&api_key={apiKey}&include_adult=true&page={page}", searchTerm, apiKey, 1)
                .retrieve()
                .toEntity(TheMovieDbSearchResponse.class)
                .block()
                .getBody();
    }

    @Override
    public Mono<TheMovieDbSearchResponse> getPageAsync(String searchTerm, int page) {
        log.debug("Collecting page {} asynchronously with search term: {}", page, searchTerm);
        WebClient webClient = WebClient.create(apiBaseUrl);
        return webClient.get()
                .uri("/search/movie?query={searchTerm}&api_key={apiKey}&include_adult=true&page={page}", searchTerm, apiKey, page)
                .retrieve()
                .bodyToMono(TheMovieDbSearchResponse.class);
    }

    @Override
    public Mono<TheMovieDbCreditsResponse> getCredits(String id) {
        log.debug("Completing movie: {}", id);
        WebClient webClient = WebClient.create(apiBaseUrl);
        return webClient.get()
                .uri("/movie/{id}/credits?api_key={apiKey}", id, apiKey)
                .retrieve()
                .bodyToMono(TheMovieDbCreditsResponse.class);
    }
}
