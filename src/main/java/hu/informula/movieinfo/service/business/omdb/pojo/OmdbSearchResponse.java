package hu.informula.movieinfo.service.business.omdb.pojo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class OmdbSearchResponse {
    @JsonProperty(value = "Search")
    private List<OmdbSearchItemResponse> search;

    @JsonProperty(value = "Response")
    private boolean response;

    @JsonProperty(value = "Error")
    private String error;

    private int totalResults;
}
