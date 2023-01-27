package com.shuttle.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
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
import org.springframework.web.client.ResourceAccessException;

import com.shuttle.common.RESTError;
import com.shuttle.credentials.dto.CredentialsDTO;
import com.shuttle.credentials.dto.TokenDTO;
import com.shuttle.location.dto.LocationDTO;
import com.shuttle.location.dto.RouteDTO;
import com.shuttle.ride.Ride.Status;
import com.shuttle.ride.dto.CreateRideDTO;
import com.shuttle.ride.dto.RideDTO;
import com.shuttle.user.dto.BasicUserInfoDTO;

@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
public class RideControllerTest {
	@Autowired
	private TestRestTemplate restTemplate;
	
	private String JWT_DRIVER_BOB = "";
	private String JWT_DRIVER_1 = "";
	private String JWT_PASSENGER_JOHN = "";
	private String JWT_PASSENGER_TROY = "";
	private String JWT_ADMIN = "";
	
	private HttpHeaders getHeader(String jwt) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		if (jwt != null) {
			headers.setBearerAuth(jwt);
		}
		
		return headers;
	}
	
	private <T, U> ResponseEntity<T> post(String URL, Long pathParamId, U body, String jwt) {
		HttpEntity<U> requestBody = new HttpEntity<U>(body, getHeader(jwt));
		ResponseEntity<T> response = restTemplate.exchange(
				URL,
				HttpMethod.POST,
				requestBody,
				new ParameterizedTypeReference<T>() {},
				pathParamId
		);
		return response;
	}
	
	private <T, U> ResponseEntity<T> put(String URL, Long pathParamId, U body, String jwt) {
		HttpEntity<U> requestBody = new HttpEntity<U>(body, getHeader(jwt));
		ResponseEntity<T> response = restTemplate.exchange(
				URL,
				HttpMethod.PUT,
				requestBody,
				new ParameterizedTypeReference<T>() {},
				pathParamId
		);
		return response;
	}
	
	@BeforeAll
	public void setup() {
		login();
		
		assertNotNull(JWT_DRIVER_BOB);
		assertNotNull(JWT_DRIVER_1);
		assertNotNull(JWT_PASSENGER_JOHN);
		assertNotNull(JWT_PASSENGER_TROY);
		assertNotNull(JWT_ADMIN);
	}
	
	private String login(String email, String password) {
		final String URL = "/api/user/login";

		CredentialsDTO payload = new CredentialsDTO(email, password);
		HttpEntity<CredentialsDTO> requestBody = new HttpEntity<CredentialsDTO>(payload, getHeader(null));
		ResponseEntity<TokenDTO> response = restTemplate.exchange(
				URL, HttpMethod.POST,requestBody,new ParameterizedTypeReference<TokenDTO>() {}
		);
		
		return response.getBody().getAccessToken();
	}
	
	public void login() {
		JWT_DRIVER_BOB = login("bob@gmail.com", "bob123");
		JWT_DRIVER_1 = login("driver1@gmail.com", "1234");
		JWT_PASSENGER_JOHN = login("john@gmail.com", "john123");
		JWT_PASSENGER_TROY = login("troy@gmail.com", "Troytroy123");
		JWT_ADMIN = login("admin@gmail.com", "admin");
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	
	@Test
	public void createRide_unauthorized() {
		final String URL = "/api/ride";

		Assertions.assertThrows(ResourceAccessException.class, new Executable() {	
			@Override
			public void execute() throws Throwable {
				ResponseEntity<RESTError> response = post(URL, null, null, null);
			}
		});
	}
	
	@Test
	public void createRide_forbidden_driver() {
		final String URL = "/api/ride";
		ResponseEntity<String> response = post(URL, null, null, JWT_DRIVER_BOB);
		assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
	}
	
	@Test
	public void createRide_forbidden_admin() {
		final String URL = "/api/ride";
		ResponseEntity<String> response = post(URL, null, null, JWT_ADMIN);
		assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
	}
	
	@Test
	public void createRide_missingFields() {
		final String URL = "/api/ride";
		CreateRideDTO dto = new CreateRideDTO();
		ResponseEntity<String> response = post(URL, null, dto, JWT_PASSENGER_TROY);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
	@Test
	public void createRide() {
		final String URL = "/api/ride";

		RouteDTO route = new RouteDTO(new LocationDTO("ABC", 45.21, 19.8), new LocationDTO("DEF", 45.22, 19.79));
		CreateRideDTO dto = new CreateRideDTO(
				Arrays.asList(new BasicUserInfoDTO(3, "troy@gmail.com")), 
				Arrays.asList(route),
				"LUXURY", true, false, null, 123.0);
		
		HttpEntity<CreateRideDTO> requestBody = new HttpEntity<CreateRideDTO>(dto, getHeader(JWT_PASSENGER_TROY));
		ResponseEntity<RideDTO> response = restTemplate.exchange(
				URL, HttpMethod.POST,requestBody,new ParameterizedTypeReference<RideDTO>() {}
		);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		
		RideDTO result = response.getBody();
		assertThat(result.getLocations()).usingRecursiveComparison().isEqualTo(dto.getLocations());
		assertThat(result.getPassengers()).usingRecursiveComparison().isEqualTo(dto.getPassengers());
		assertThat(result.getVehicleType()).isEqualTo(dto.getVehicleType());
		assertThat(result.getBabyTransport().equals(dto.getBabyTransport()));
		assertThat(result.getPetTransport().equals(dto.getPetTransport()));
		assertThat(result.getTotalLength()).isEqualTo(dto.getDistance());
		assertThat(result.getDriver()).isNotNull();
		assertThat(result.getId()).isNotNull();
		assertThat(result.getStatus().equals(Status.PENDING));
		assertThat(result.getRejection()).isNull();
	}
	
	@Test
	public void createRide_alreadyPending() { 
		final String URL = "/api/ride";

		RouteDTO route = new RouteDTO(new LocationDTO("ABC", 45.21, 19.8), new LocationDTO("DEF", 45.22, 19.79));
		CreateRideDTO dto = new CreateRideDTO(
				Arrays.asList(new BasicUserInfoDTO(3, "troy@gmail.com")), 
				Arrays.asList(route),
				"STANDARD", true, false, null, 123.0);
		
		HttpEntity<CreateRideDTO> requestBody = new HttpEntity<CreateRideDTO>(dto, getHeader(JWT_PASSENGER_JOHN));
		ResponseEntity<RESTError> response = restTemplate.exchange(
				URL, HttpMethod.POST,requestBody,new ParameterizedTypeReference<RESTError>() {}
		);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("Cannot create a ride while you have one already pending!", response.getBody().getMessage());
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////

	@ParameterizedTest
	@ValueSource(longs = {-74389, -1, 38923829})
	public void acceptRide_noRide(Long rideId) {
		final String URL = "/api/ride/{id}/accept";
		HttpEntity<Void> requestBody = new HttpEntity<Void>(null, getHeader(JWT_DRIVER_BOB));
		
		ResponseEntity<String> response = restTemplate.exchange(
				URL,
				HttpMethod.PUT,
				requestBody,
				new ParameterizedTypeReference<String>() {},
				rideId
		);
		
		//ResponseEntity<String> response = put(URL, requestBody, rideId);
		// TODO: put here throws an error.
	
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertEquals("Ride does not exist!", response.getBody());
	}

	@Test
	public void acceptRide_unauthorized() {
		final String URL = "/api/ride/{id}/accept";

		Assertions.assertThrows(ResourceAccessException.class, new Executable() {	
			@Override
			public void execute() throws Throwable {
				ResponseEntity<RESTError> response = post(URL, 1L, null, null);
			}
		});
	}
	
	@Test
	public void acceptRide_forbidden() {
		final String URL = "/api/ride/{id}/accept";
		ResponseEntity<String> response = put(URL, 1L, null, JWT_PASSENGER_JOHN);
		assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
	}
	
	@Test
	public void acceptRide_ofOtherDriver() {
	}
}
