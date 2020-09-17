package hu.informula.movieinfo.service.business.themoviedb.pojo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class TheMovieDbCreditsResponse {
    private String id;
    private List<TheMovieDbCreditItemResponse> crew;
}
