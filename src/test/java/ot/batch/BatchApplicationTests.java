package ot.batch;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ot.batch.api.open.service.OpenApiService;

@SpringBootTest
class BatchApplicationTests {

	@Autowired
	private OpenApiService openApiService;

	@Test
	void contextLoads() {
		System.out.println(openApiService.requestAreaBased(12, 3, 1));

	}

}
