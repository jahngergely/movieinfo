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
public class OmdbSearchItemResponse {
    @JsonProperty(value = "Title")
    private String title;
    @JsonProperty(value = "imdbID")
    private String imdbId;
    @JsonProperty(value = "Type")
    private String type;
}
