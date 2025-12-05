package com.portfolio.flotrack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class FlotrackApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlotrackApplication.class, args);
	}

}
