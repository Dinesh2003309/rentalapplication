package com.rental;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.unlogged.Unlogged;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
@EnableAutoConfiguration
public class RentalServiceApplication {

	/**
	 * Returns a WebMvcConfigurer instance with Cross-Origin Resource Sharing (CORS) configuration applied.
	 * CORS is a mechanism that allows resources on a web page to be requested from another domain outside the domain from which the resource originated.
	 *
	 * @return a WebMvcConfigurer instance with CORS configuration applied
	 */
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedHeaders("Origin", "X-Requested-With", "Content-Type", "Accept", "Authorization")
						.allowedOrigins("http://localhost:8200")
						.allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
						.exposedHeaders("X-Get-Header")
						.allowCredentials(true).maxAge(3600);
			}
		};
	}

	@Unlogged
	public static void main(String[] args) {
		SpringApplication.run(RentalServiceApplication.class, args);
	}

}
 