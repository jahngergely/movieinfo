package hu.informula.movieinfo.service;

import java.util.List;

import hu.informula.movieinfo.service.pojo.Movie;
import hu.informula.movieinfo.utils.ApiType;

public interface MovieInfoService {
    boolean hasApiType(ApiType apiType);

    List<Movie> getMovieList(String searchTerm);
}
