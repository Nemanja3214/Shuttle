package com.shuttle.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.shuttle.credentials.dto.CredentialsDTO;
import com.shuttle.credentials.dto.TokenDTO;

@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
public class RideControllerTest {
	@Autowired
	private TestRestTemplate restTemplate;
	
	private String JWT_DRIVER = "";
	private String JWT_PASSENGER = "";
	
	private HttpHeaders getHeader(String jwt) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		if (jwt != null) {
			headers.setBearerAuth(jwt);
		}
		
		return headers;
	}
	
	@BeforeAll
	public void setup() {
		login();
		
		assertNotNull(JWT_DRIVER);
		assertNotNull(JWT_PASSENGER);
	}
	
	public void login() {
		final String URL = "/api/user/login";
		CredentialsDTO payload = null;
		HttpEntity<CredentialsDTO> requestBody = null;
		ResponseEntity<TokenDTO> response = null;
		
		payload = new CredentialsDTO("bob@gmail.com", "bob123");
		requestBody = new HttpEntity<CredentialsDTO>(payload, getHeader(null));
		response = restTemplate.exchange(
				URL, 
				HttpMethod.POST,
				requestBody,
				new ParameterizedTypeReference<TokenDTO>() {}
		);
		JWT_DRIVER = response.getBody().getAccessToken();
		
		payload = new CredentialsDTO("john@gmail.com", "john123");
		requestBody = new HttpEntity<CredentialsDTO>(payload, getHeader(null));
		response = restTemplate.exchange(
				URL, 
				HttpMethod.POST,
				requestBody,
				new ParameterizedTypeReference<TokenDTO>() {}
		);
		JWT_PASSENGER = response.getBody().getAccessToken();
	}
	
	@ParameterizedTest
	@ValueSource(longs = {-74389, -1, 0, 38923829})
	public void acceptRide_noRide(Long rideId) {
		final String URL = "/api/ride/{id}/accept";
		HttpEntity<Void> requestBody = new HttpEntity<Void>((Void)(null), getHeader(JWT_DRIVER));
		
		ResponseEntity<String> response = restTemplate.exchange(
				URL,
				HttpMethod.PUT,
				requestBody,
				new ParameterizedTypeReference<String>() {},
				rideId
		);
		
		assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
		assertEquals(response.getBody(), "Ride does not exist!");
	}
}
