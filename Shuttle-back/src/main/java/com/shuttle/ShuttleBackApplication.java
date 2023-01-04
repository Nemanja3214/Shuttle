package com.shuttle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ShuttleBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShuttleBackApplication.class, args);
	}
	
	@GetMapping("/hello")
	@CrossOrigin
	public String helloWorld() {
		return "Hello from Spring Boot";
	}
}
