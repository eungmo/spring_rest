package io.simondev.demoinflearnrestapi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
// 이거를 줘야지 test.resources.application-test.properties를
// application.properties 위에 덮어써서,
// H2 설정이 적용된다.
@ActiveProfiles("test")
public class DemoInflearnRestApiApplicationTests {

	@Test
	public void contextLoads() {
	}

}

