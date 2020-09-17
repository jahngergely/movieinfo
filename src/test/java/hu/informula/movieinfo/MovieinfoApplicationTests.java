package hu.informula.movieinfo;

import hu.informula.movieinfo.controller.MovieInfoController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class MovieinfoApplicationTests {

	@Autowired
	private MovieInfoController movieInfoController;

	@Test
	void contextLoads() {
		assertNotNull(movieInfoController);
	}
}
