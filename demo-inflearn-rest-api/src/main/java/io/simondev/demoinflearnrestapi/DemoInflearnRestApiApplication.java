package io.simondev.demoinflearnrestapi;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoInflearnRestApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoInflearnRestApiApplication.class, args);
	}

	// 공용으로 쓸 것이기 때문에 빈으로 등록하자
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
}

