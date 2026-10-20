package io.grundner.wallbox;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "w5x.settings-file=build/tmp/context-test/settings.json")
class WallboxReportApplicationTests {

	@Test
	void contextLoads() {
	}

}
