package org.example.like;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"org.example.like", "org.example.exception"})
public class LikeApplication {

	public static void main(String[] args) {
		SpringApplication.run(LikeApplication.class, args);
	}

}
