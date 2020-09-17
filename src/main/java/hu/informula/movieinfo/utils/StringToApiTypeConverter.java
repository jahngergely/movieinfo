package hu.informula.movieinfo.utils;

import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

public class StringToApiTypeConverter implements Converter<String, ApiType> {
    @Override
    public ApiType convert(String source) {
        return ApiType.valueOf(source.toUpperCase());
    }
}
