package com.openApi2.OpenApi2;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class OpenApi2Application {

	public static void main(String[] args) {
		SpringApplication.run(OpenApi2Application.class, args);
	}
	@Bean
	public OpenAPI customOpenAPI(){
		return new OpenAPI().info(new Info().title("Api Couch").version("1.0.0").description("Esta API es la segunda"));
	}
}
