package hu.informula.movieinfo.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import hu.informula.movieinfo.utils.StringToApiTypeConverter;

@Configuration
public class FormatterConfiguration implements WebMvcConfigurer{
    
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToApiTypeConverter());
	}
}
