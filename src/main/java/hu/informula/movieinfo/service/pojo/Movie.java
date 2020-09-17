package hu.informula.movieinfo.service.pojo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Movie {
    @JsonIgnore
    private String id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "Title")
    private String title;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty(value = "Director")
    private List<String> director;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "Year")
    private String year;

    @JsonFormat(with = JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)
    public List<String> getDirector() {
        return director;
    }
}
