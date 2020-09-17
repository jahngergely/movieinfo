package hu.informula.movieinfo.service.business.themoviedb.pojo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class TheMovieDbSearchResponse {
    private List<TheMovieDbSearchItemResponse> results;
    private int page;

    @JsonProperty(value = "total_results")
    private int totalResults;

    @JsonProperty(value = "total_pages")
    private int totalPages;

    private boolean success;
    @JsonProperty(value = "status_code")
    private int statusCode;
    @JsonProperty(value = "status_message")
    private String statusMessage;
}