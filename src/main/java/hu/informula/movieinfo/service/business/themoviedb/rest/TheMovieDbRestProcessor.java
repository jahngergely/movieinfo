package hu.informula.movieinfo.service.business.themoviedb.rest;

import hu.informula.movieinfo.service.business.themoviedb.pojo.TheMovieDbCreditsResponse;
import hu.informula.movieinfo.service.business.themoviedb.pojo.TheMovieDbSearchResponse;
import reactor.core.publisher.Mono;


public interface TheMovieDbRestProcessor {
    Mono<TheMovieDbCreditsResponse> getCredits(String id);
    TheMovieDbSearchResponse getFirstPageSync(String searchTerm);
    Mono<TheMovieDbSearchResponse> getPageAsync(String searchTerm, int page);
}
