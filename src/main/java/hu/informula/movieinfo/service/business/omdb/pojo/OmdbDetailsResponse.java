package hu.informula.movieinfo.service.business.omdb.pojo;

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
public class OmdbDetailsResponse {
    @JsonProperty(value = "Response")
    private boolean response;
    @JsonProperty(value = "Error")
    private String error;

    @JsonProperty(value = "Title")
    private String title;

    @JsonProperty(value = "Director")
    private String director;

    @JsonProperty(value = "Year")
    private String year;
}
