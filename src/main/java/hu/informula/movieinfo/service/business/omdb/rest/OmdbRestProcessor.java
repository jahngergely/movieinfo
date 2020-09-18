package hu.informula.movieinfo.service.business.omdb.rest;

import hu.informula.movieinfo.service.business.omdb.pojo.OmdbDetailsResponse;
import hu.informula.movieinfo.service.business.omdb.pojo.OmdbSearchResponse;
import reactor.core.publisher.Mono;

public interface OmdbRestProcessor {
    Mono<OmdbDetailsResponse> getDetails(String id);
    OmdbSearchResponse getFirstPageSync(String searchTerm);
    Mono<OmdbSearchResponse> getPageAsync(String searchTerm, int page);
}
