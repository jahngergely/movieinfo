package hu.informula.movieinfo.service.business.themoviedb.rest;

import hu.informula.movieinfo.service.business.themoviedb.pojo.TheMovieDbCreditsResponse;
import hu.informula.movieinfo.service.business.themoviedb.pojo.TheMovieDbSearchResponse;


public interface TheMovieDbRestProcessor {
    TheMovieDbCreditsResponse getCredits(String id);
    TheMovieDbSearchResponse getMovies(String searchTerm, int page);
}
