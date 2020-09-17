package hu.informula.movieinfo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(scanBasePackages = "hu.informula")
@EnableJpaAuditing
@EnableCaching
public class MovieinfoApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(MovieinfoApplication.class, args);
	}

}
