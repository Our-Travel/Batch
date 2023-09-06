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
		int a = openApiService.maxPage(15, 126.981611, 37.568477);
		System.out.println(a);
	}

}
