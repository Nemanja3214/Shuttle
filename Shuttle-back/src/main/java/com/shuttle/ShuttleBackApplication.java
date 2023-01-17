package com.shuttle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.shuttle.common.RESTError;

@ControllerAdvice
@SpringBootApplication
@RestController
public class ShuttleBackApplication extends ResponseEntityExceptionHandler {

	public static void main(String[] args) {
		SpringApplication.run(ShuttleBackApplication.class, args);
	}
	
	@GetMapping("/hello")
	@CrossOrigin(origins = "http://localhost:4200")
	public String helloWorld() {
		return "Hello from Spring Boot";
	}

    @ExceptionHandler(AccessDeniedException.class)
    public final ResponseEntity<?> handleAccessDeniedException(Exception ex, WebRequest request) {
        return new ResponseEntity<RESTError>(new RESTError("Access denied!"), HttpStatus.FORBIDDEN);
    }
}
