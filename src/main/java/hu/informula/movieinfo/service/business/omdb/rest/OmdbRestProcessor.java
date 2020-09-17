package hu.informula.movieinfo.service.business.omdb.rest;

import hu.informula.movieinfo.service.business.omdb.pojo.OmdbDetailsResponse;
import hu.informula.movieinfo.service.business.omdb.pojo.OmdbSearchResponse;

public interface OmdbRestProcessor {
    OmdbDetailsResponse getDetails(String id);
    OmdbSearchResponse getMovies(String searchTerm, int page);
}
