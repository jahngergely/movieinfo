package hu.informula.movieinfo.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hu.informula.movieinfo.persistence.entity.RequestHistoryEntity;
import hu.informula.movieinfo.persistence.service.RequestHistoryCrudService;
import hu.informula.movieinfo.service.MovieInfoService;
import hu.informula.movieinfo.service.pojo.Movie;
import hu.informula.movieinfo.utils.ApiType;
import hu.informula.movieinfo.utils.HttpReqRespUtils;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@RestController
@Slf4j
@RequestMapping("movies")
public class MovieInfoController {

    @Autowired
    private List<MovieInfoService> movieServices;

    @Autowired
    private RequestHistoryCrudService requestHistoryCrudService;

    @GetMapping("/{searchTerm}")
    @Cacheable(value="movieSearch", key="new org.springframework.cache.interceptor.SimpleKey(#api, #searchTerm)")
    public String getAllMovieBySearchTermAndProvider(@RequestHeader(value = "User-Agent", required = false) String userAgent,
                                                     @RequestHeader(value = "Host", required = false) String host,
                                                     @PathVariable @NotBlank @Size(min = 1) final String searchTerm,
                                                     @RequestParam("api") final ApiType api) throws IOException, NotImplementedException {

        String ipAddress = HttpReqRespUtils.getClientIpAddressIfServletRequestExist();

        log.info("New request [SEARCH_TERM: {}, API: {}, HOST: {},  IP:{}]", searchTerm, api.toString(), host, ipAddress);

        RequestHistoryEntity requestHistoryEntity = RequestHistoryEntity.builder()
                .host(host)
                .api(api.name())
                .ipAddress(ipAddress)
                .searchTerm(searchTerm)
                .userAgent(userAgent).build();

        requestHistoryCrudService.save(requestHistoryEntity);

        final MovieInfoService movieInfoService = getMatchingMovieInfoService(api);
        final List<Movie> resultList = movieInfoService.getMovieList(searchTerm);
        final ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String jsonResult = objectWriter.writeValueAsString(resultList);
        log.info("API RESPONSE: {}", jsonResult);
        requestHistoryEntity.setFinishedAt(new Date());

        requestHistoryCrudService.save(requestHistoryEntity);

        return String.format("{\"movies\": %s}", jsonResult);
    }

    private MovieInfoService getMatchingMovieInfoService(ApiType api) throws NotImplementedException {
        return movieServices.stream().filter((movieInfoService) -> movieInfoService.hasApiType(api)).findAny()
                .orElseThrow(() -> new NotImplementedException("Api " + api.toString() + " is not implemented"));
    }

}
