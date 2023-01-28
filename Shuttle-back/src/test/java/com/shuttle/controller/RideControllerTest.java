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
import org.springframework.beans.factory.annotation.Value;
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
import com.shuttle.ride.dto.RideDriverDTO;
import com.shuttle.ride.dto.RidePassengerDTO;
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
	private String JWT_DRIVER_2 = "";
	private String JWT_DRIVER_3 = "";
	private String JWT_PASSENGER_JOHN = "";
	private String JWT_PASSENGER_TROY = "";
	private String JWT_ADMIN = "";
	private String JWT_PASSENGER_1 = "";

	private RideDTO ride1;
	private RideDTO ride2;
	private RideDTO ride3;
	
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
		initRides();
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
		JWT_DRIVER_3 = login("driver3@gmail.com", "1234");
		JWT_DRIVER_2 = login("driver2@gmail.com", "1234");
		JWT_PASSENGER_JOHN = login("john@gmail.com", "john123");
		JWT_PASSENGER_TROY = login("troy@gmail.com", "Troytroy123");
		JWT_ADMIN = login("admin@gmail.com", "admin");
		JWT_PASSENGER_1 = login("p1@gmail.com", "1234");
	}
	
	private void initRides() {
		ride1 = new RideDTO();
		ride1.setId(1L);
		ride1.setEstimatedTimeInMinutes(100);
		ride1.setBabyTransport(false);
		ride1.setPetTransport(true);
		ride1.setVehicleType("STANDARD");
		ride1.setStatus(Status.PENDING);
		ride1.setTotalCost(123.4);
		ride1.setTotalLength(5.6);
		ride1.setRejection(null);
		ride1.setScheduledTime(null);
		ride1.setStartTime(null);
		ride1.setEndTime(null);
		ride1.setLocations(Arrays.asList(new RouteDTO(new LocationDTO("Novi Sad 1", 45.235820, 19.803677), new LocationDTO("Novi Sad 2", 45.233752, 19.816665))));
		ride1.setPassengers(Arrays.asList(new RidePassengerDTO(2L, "john@gmail.com")));
		ride1.setDriver(new RideDriverDTO(1L, "bob@gmail.com"));
		
		ride2 = new RideDTO();
		ride2.setId(2L);
		ride2.setEstimatedTimeInMinutes(100);
		ride2.setBabyTransport(false);
		ride2.setPetTransport(true);
		ride2.setVehicleType("STANDARD");
		ride2.setStatus(Status.PENDING);
		ride2.setTotalCost(123.4);
		ride2.setTotalLength(5.6);
		ride2.setRejection(null);
		ride2.setScheduledTime(null);
		ride2.setStartTime(null);
		ride2.setEndTime(null);
		ride2.setLocations(Arrays.asList(new RouteDTO(new LocationDTO("Novi Sad 1", 45.235820, 19.803677), new LocationDTO("Novi Sad 2", 45.233752, 19.816665))));
		ride2.setPassengers(Arrays.asList(new RidePassengerDTO(12L, "p2@gmail.com")));
		ride2.setDriver(new RideDriverDTO(7L, "driver3@gmail.com"));
		
		ride3 = new RideDTO();
		ride3.setId(3L);
		ride3.setEstimatedTimeInMinutes(100);
		ride3.setBabyTransport(false);
		ride3.setPetTransport(false);
		ride3.setVehicleType("STANDARD");
		ride3.setStatus(Status.ACCEPTED);
		ride3.setTotalCost(123.4);
		ride3.setTotalLength(5.6);
		ride3.setRejection(null);
		ride3.setScheduledTime(null);
		ride3.setStartTime(null);
		ride3.setEndTime(null);
		ride3.setLocations(Arrays.asList(new RouteDTO(new LocationDTO("Novi Sad 1", 45.235820, 19.803677), new LocationDTO("Novi Sad 2", 45.233752, 19.816665))));
		ride3.setPassengers(Arrays.asList(new RidePassengerDTO(12L, "p2@gmail.com")));
		ride3.setDriver(new RideDriverDTO(6L, "driver2@gmail.com"));
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
	
	@Test
	public void createRide_noDriverAvailable_unsupportedParams() { 
		final String URL = "/api/ride";

		RouteDTO route = new RouteDTO(new LocationDTO("ABC", 45.21, 19.8), new LocationDTO("DEF", 45.22, 19.79));
		CreateRideDTO dto = new CreateRideDTO(
				Arrays.asList(new BasicUserInfoDTO(11, "p1@gmail.com")), 
				Arrays.asList(route),
				"VAN", true, true, null, 123.0); // There is no van with babies and pets.
		
		HttpEntity<CreateRideDTO> requestBody = new HttpEntity<CreateRideDTO>(dto, getHeader(JWT_PASSENGER_1));
		ResponseEntity<RESTError> response = restTemplate.exchange(
				URL, HttpMethod.POST,requestBody,new ParameterizedTypeReference<RESTError>() {}
		);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("No driver available!", response.getBody().getMessage());
	}
	
	@Test
	public void createRide_noDriverAvailable_supportedParamsBusy() { 
		final String URL = "/api/ride";

		RouteDTO route = new RouteDTO(new LocationDTO("ABC", 45.21, 19.8), new LocationDTO("DEF", 45.22, 19.79));
		CreateRideDTO dto = new CreateRideDTO(
				Arrays.asList(new BasicUserInfoDTO(11, "p1@gmail.com")), 
				Arrays.asList(route),
				"STANDARD", true, false, null, 123.0); // Standard vehicle - bob - busy
		
		HttpEntity<CreateRideDTO> requestBody = new HttpEntity<CreateRideDTO>(dto, getHeader(JWT_PASSENGER_1));
		ResponseEntity<RESTError> response = restTemplate.exchange(
				URL, HttpMethod.POST,requestBody,new ParameterizedTypeReference<RESTError>() {}
		);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("No driver available!", response.getBody().getMessage());
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////
		
	@Test
	public void getActiveRideByDriver_unauthorized() {
		final String URL = "/api/ride/driver/{id}/active";
		Long driverId = 1L;

		HttpEntity<Void> requestBody = new HttpEntity<Void>(null, getHeader(null));
		ResponseEntity<RESTError> response = restTemplate.exchange(URL, HttpMethod.GET, requestBody, new ParameterizedTypeReference<RESTError>() {}, driverId);
		
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
	}
	
	@Test
	public void getActiveRideByDriver_forbidden_passenger() {
		final String URL = "/api/ride/driver/{id}/active";
		Long driverId = 1L;

		HttpEntity<Void> requestBody = new HttpEntity<Void>(null, getHeader(JWT_PASSENGER_JOHN));
		ResponseEntity<RESTError> response = restTemplate.exchange(URL, HttpMethod.GET, requestBody, new ParameterizedTypeReference<RESTError>() {}, driverId);
		
		assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
	}
	
	@Test
	public void getActiveRideByDriver_driverCannotSeeRideOfOtherDriver() {
		final String URL = "/api/ride/driver/{id}/active";
		Long driverId = 5L;

		HttpEntity<Void> requestBody = new HttpEntity<Void>(null, getHeader(JWT_DRIVER_BOB));
		ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.GET, requestBody, new ParameterizedTypeReference<String>() {}, driverId);
		
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertEquals("Active ride does not exist!", response.getBody());
	}
	
	@Test
	public void getActiveRideByDriver_driverNotFound() {
		final String URL = "/api/ride/driver/{id}/active";
		Long driverId = 0L;

		HttpEntity<Void> requestBody = new HttpEntity<Void>(null, getHeader(JWT_ADMIN));
		ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.GET, requestBody, new ParameterizedTypeReference<String>() {}, driverId);
		
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertEquals("Active ride does not exist!", response.getBody());
	}
	
	@Test
	public void getActiveRideByDriver_noRide() {
		final String URL = "/api/ride/driver/{id}/active";
		Long driverId = 9L;

		HttpEntity<Void> requestBody = new HttpEntity<Void>(null, getHeader(JWT_ADMIN));
		ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.GET, requestBody, new ParameterizedTypeReference<String>() {}, driverId);
		
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertEquals("Active ride does not exist!", response.getBody());
	}
	
	@Test
	public void getActiveRideByDriver() {
		final String URL = "/api/ride/driver/{id}/active";
		Long driverId = 1L;

		HttpEntity<Void> requestBody = new HttpEntity<Void>(null, getHeader(JWT_DRIVER_BOB));
		ResponseEntity<RideDTO> response = restTemplate.exchange(URL, HttpMethod.GET, requestBody, new ParameterizedTypeReference<RideDTO>() {}, driverId);
		
		assertEquals(HttpStatus.OK, response.getStatusCode());

		RideDTO result = response.getBody();
		RideDTO expected = ride1;
		assertThat(result).usingRecursiveComparison().isEqualTo(expected);
	}
	
	@Test
	public void getActiveRideByDriver_adminCanSeeRideOfAnyDriver() {
		final String URL = "/api/ride/driver/{id}/active";
		Long driverId = 1L;

		HttpEntity<Void> requestBody = new HttpEntity<Void>(null, getHeader(JWT_ADMIN));
		ResponseEntity<RideDTO> response = restTemplate.exchange(URL, HttpMethod.GET, requestBody, new ParameterizedTypeReference<RideDTO>() {}, driverId);
		
		assertEquals(HttpStatus.OK, response.getStatusCode());

		RideDTO result = response.getBody();
		RideDTO expected = ride1;
		assertThat(result).usingRecursiveComparison().isEqualTo(expected);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void getActiveRideByPassenger_unauthorized() {
		final String URL = "/api/ride/passenger/{id}/active";
		Long passengerId = 2L;

		HttpEntity<Void> requestBody = new HttpEntity<Void>(null, getHeader(null));
		ResponseEntity<RESTError> response = restTemplate.exchange(URL, HttpMethod.GET, requestBody, new ParameterizedTypeReference<RESTError>() {}, passengerId);
		
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
	}
	
	@Test
	public void getActiveRideByPassenger_forbidden_driver() {
		final String URL = "/api/ride/passenger/{id}/active";
		Long passengerId = 2L;

		HttpEntity<Void> requestBody = new HttpEntity<Void>(null, getHeader(JWT_DRIVER_BOB));
		ResponseEntity<RESTError> response = restTemplate.exchange(URL, HttpMethod.GET, requestBody, new ParameterizedTypeReference<RESTError>() {}, passengerId);
		
		assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
	}
	
	@Test
	public void getActiveRideByPassenger_driverCannotSeeRideOfOtherPassenger() {
		final String URL = "/api/ride/passenger/{id}/active";
		Long passengerId = 3L;

		HttpEntity<Void> requestBody = new HttpEntity<Void>(null, getHeader(JWT_PASSENGER_JOHN));
		ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.GET, requestBody, new ParameterizedTypeReference<String>() {}, passengerId);
		
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertEquals("Active ride does not exist!", response.getBody());
	}
	
	@Test
	public void getActiveRideByPassenger_passengerNotFound() {
		final String URL = "/api/ride/passenger/{id}/active";
		Long passengerId = 0L;

		HttpEntity<Void> requestBody = new HttpEntity<Void>(null, getHeader(JWT_ADMIN));
		ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.GET, requestBody, new ParameterizedTypeReference<String>() {}, passengerId);
		
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertEquals("Active ride does not exist!", response.getBody());
	}
	
	@Test
	public void getActiveRideByPassenger_noRide() {
		final String URL = "/api/ride/passenger/{id}/active";
		Long passengerId = 3L;

		HttpEntity<Void> requestBody = new HttpEntity<Void>(null, getHeader(JWT_PASSENGER_1));
		ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.GET, requestBody, new ParameterizedTypeReference<String>() {}, passengerId);
		
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertEquals("Active ride does not exist!", response.getBody());
	}
	
	@Test
	public void getActiveRideByPassenger() {
		final String URL = "/api/ride/passenger/{id}/active";
		Long passengerId = 2L;

		HttpEntity<Void> requestBody = new HttpEntity<Void>(null, getHeader(JWT_PASSENGER_JOHN));
		ResponseEntity<RideDTO> response = restTemplate.exchange(URL, HttpMethod.GET, requestBody, new ParameterizedTypeReference<RideDTO>() {}, passengerId);
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		
		RideDTO result = response.getBody();
		RideDTO expected = ride1;
		assertThat(result).usingRecursiveComparison().isEqualTo(expected);
	}
	
	@Test
	public void getActiveRideByPassenger_adminCanSeeRideOfAnyPassenger() {
		final String URL = "/api/ride/passenger/{id}/active";
		Long passengerId = 2L;

		HttpEntity<Void> requestBody = new HttpEntity<Void>(null, getHeader(JWT_PASSENGER_JOHN));
		ResponseEntity<RideDTO> response = restTemplate.exchange(URL, HttpMethod.GET, requestBody, new ParameterizedTypeReference<RideDTO>() {}, passengerId);
		
		assertEquals(HttpStatus.OK, response.getStatusCode());

		RideDTO result = response.getBody();
		RideDTO expected = ride1;
		assertThat(result).usingRecursiveComparison().isEqualTo(expected);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	
	@Test
	public void getRide_unauthorized() {
		final String URL = "/api/ride/{id}";
		Long rideId = 1L;

		HttpEntity<Void> requestBody = new HttpEntity<Void>(null, getHeader(null));
		ResponseEntity<RESTError> response = restTemplate.exchange(URL, HttpMethod.GET, requestBody, new ParameterizedTypeReference<RESTError>() {}, rideId);
		
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
	}
	
	@ParameterizedTest
	@ValueSource(longs = {-5, 0, 47389})
	public void getRide_notFound(Long rideId) {
		final String URL = "/api/ride/{id}";

		HttpEntity<Void> requestBody = new HttpEntity<Void>(null, getHeader(JWT_ADMIN));
		ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.GET, requestBody, new ParameterizedTypeReference<String>() {}, rideId);
		
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertEquals("Ride does not exist!", response.getBody());
	}
	
	@Test
	public void getRide_driverCannotSeeRidesOfOtherUsers() {
		final String URL = "/api/ride/{id}";
		Long rideId = 1L;

		HttpEntity<Void> requestBody = new HttpEntity<Void>(null, getHeader(JWT_DRIVER_1));
		ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.GET, requestBody, new ParameterizedTypeReference<String>() {}, rideId);
		
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
	
	@Test
	public void getRide_passengerCannotSeeRidesOfOtherUsers() {
		final String URL = "/api/ride/{id}";
		Long rideId = 1L;

		HttpEntity<Void> requestBody = new HttpEntity<Void>(null, getHeader(JWT_PASSENGER_TROY));
		ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.GET, requestBody, new ParameterizedTypeReference<String>() {}, rideId);
		
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
	
	@Test
	public void getRide_asDriver() {
		final String URL = "/api/ride/{id}";
		Long rideId = 1L;

		HttpEntity<Void> requestBody = new HttpEntity<Void>(null, getHeader(JWT_DRIVER_BOB));
		ResponseEntity<RideDTO> response = restTemplate.exchange(URL, HttpMethod.GET, requestBody, new ParameterizedTypeReference<RideDTO>() {}, rideId);
		
		assertEquals(HttpStatus.OK, response.getStatusCode());

		RideDTO result = response.getBody();
		RideDTO expected = ride1;
		assertThat(result).usingRecursiveComparison().isEqualTo(expected);
	}
	
	@Test
	public void getRide_asPassenger() {
		final String URL = "/api/ride/{id}";
		Long rideId = 1L;

		HttpEntity<Void> requestBody = new HttpEntity<Void>(null, getHeader(JWT_PASSENGER_JOHN));
		ResponseEntity<RideDTO> response = restTemplate.exchange(URL, HttpMethod.GET, requestBody, new ParameterizedTypeReference<RideDTO>() {}, rideId);
		
		assertEquals(HttpStatus.OK, response.getStatusCode());

		RideDTO result = response.getBody();
		RideDTO expected = ride1;
		assertThat(result).usingRecursiveComparison().isEqualTo(expected);
	}
	
	@Test
	public void getRide_asAdmin() {
		final String URL = "/api/ride/{id}";
		Long rideId = 1L;

		HttpEntity<Void> requestBody = new HttpEntity<Void>(null, getHeader(JWT_ADMIN));
		ResponseEntity<RideDTO> response = restTemplate.exchange(URL, HttpMethod.GET, requestBody, new ParameterizedTypeReference<RideDTO>() {}, rideId);
		
		assertEquals(HttpStatus.OK, response.getStatusCode());

		RideDTO result = response.getBody();
		RideDTO expected = ride1;
		assertThat(result).usingRecursiveComparison().isEqualTo(expected);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	
	@Test
	public void acceptRide_unauthorized() {
		final String URL = "/api/ride/{id}/accept";
		Long rideId = 2L;

		Assertions.assertThrows(ResourceAccessException.class, new Executable() {	
			@Override
			public void execute() throws Throwable {	
				HttpEntity<Void> requestBody = new HttpEntity<Void>(null, getHeader(null));
				ResponseEntity<RESTError> response = restTemplate.exchange(URL, HttpMethod.POST, requestBody, new ParameterizedTypeReference<RESTError>() {}, rideId);			
			}
		});
	}
	
	@Test
	public void acceptRide_forbidden_admin() {
		final String URL = "/api/ride/{id}/accept";
		Long rideId = 2L;
		
		HttpEntity<Void> requestBody = new HttpEntity<Void>(null, getHeader(JWT_ADMIN));
		ResponseEntity<RESTError> response = restTemplate.exchange(URL, HttpMethod.PUT, requestBody, new ParameterizedTypeReference<RESTError>() {}, rideId);	
		
		assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
	}
	
	@Test
	public void acceptRide_forbidden_passenger() {
		final String URL = "/api/ride/{id}/accept";
		Long rideId = 2L;
		
		HttpEntity<Void> requestBody = new HttpEntity<Void>(null, getHeader(JWT_PASSENGER_1));
		ResponseEntity<RESTError> response = restTemplate.exchange(URL, HttpMethod.PUT, requestBody, new ParameterizedTypeReference<RESTError>() {}, rideId);	
		
		assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
	}
	
	@Test
	public void acceptRide_notFound() {
		final String URL = "/api/ride/{id}/accept";
		Long rideId = 0L;
		
		HttpEntity<Void> requestBody = new HttpEntity<Void>(null, getHeader(JWT_DRIVER_3));
		ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.PUT, requestBody, new ParameterizedTypeReference<String>() {}, rideId);	
		
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertEquals("Ride does not exist!", response.getBody());
	}
	
	@Test
	public void acceptRide_driverCannotAcceptRideOfOtherDriver() {
		final String URL = "/api/ride/{id}/accept";
		Long rideId = 2L;
		
		HttpEntity<Void> requestBody = new HttpEntity<Void>(null, getHeader(JWT_DRIVER_1));
		ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.PUT, requestBody, new ParameterizedTypeReference<String>() {}, rideId);	
		
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertEquals("Ride does not exist!", response.getBody());
	}
	
	@Test
	public void acceptRide_notPending() {
		final String URL = "/api/ride/{id}/accept";
		Long rideId = 3L;
		
		HttpEntity<Void> requestBody = new HttpEntity<Void>(null, getHeader(JWT_DRIVER_2));
		ResponseEntity<RESTError> response = restTemplate.exchange(URL, HttpMethod.PUT, requestBody, new ParameterizedTypeReference<RESTError>() {}, rideId);	
		
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("Cannot accept a ride that is not in status PENDING!", response.getBody().getMessage());
	}
	
	@Test
	public void acceptRide() {
		final String URL = "/api/ride/{id}/accept";
		Long rideId = 2L;
		
		HttpEntity<Void> requestBody = new HttpEntity<Void>(null, getHeader(JWT_DRIVER_3));
		ResponseEntity<RideDTO> response = restTemplate.exchange(URL, HttpMethod.PUT, requestBody, new ParameterizedTypeReference<RideDTO>() {}, rideId);	
		
		assertEquals(HttpStatus.OK, response.getStatusCode());

		RideDTO expected = ride2;
		RideDTO result = response.getBody();
		
		assertThat(result).usingRecursiveComparison().ignoringFields("status").isEqualTo(expected);
		assertThat(result.getStatus()).isEqualTo(Status.ACCEPTED);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	
	public void startRide_unauthorized() {
		final String URL = "/api/ride/{id}/start";
		Long rideId = 3L;

		Assertions.assertThrows(ResourceAccessException.class, new Executable() {	
			@Override
			public void execute() throws Throwable {	
				HttpEntity<Void> requestBody = new HttpEntity<Void>(null, getHeader(null));
				ResponseEntity<RESTError> response = restTemplate.exchange(URL, HttpMethod.POST, requestBody, new ParameterizedTypeReference<RESTError>() {}, rideId);			
			}
		});
	}
	
	public void startRide_forbidden_admin() {
		final String URL = "/api/ride/{id}/start";
		Long rideId = 3L;
		
		HttpEntity<Void> requestBody = new HttpEntity<Void>(null, getHeader(JWT_ADMIN));
		ResponseEntity<RESTError> response = restTemplate.exchange(URL, HttpMethod.PUT, requestBody, new ParameterizedTypeReference<RESTError>() {}, rideId);	
		
		assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
	}
	
	public void startRide_forbidden_passenger() {
		final String URL = "/api/ride/{id}/start";
		Long rideId = 3L;
		
		HttpEntity<Void> requestBody = new HttpEntity<Void>(null, getHeader(JWT_PASSENGER_1));
		ResponseEntity<RESTError> response = restTemplate.exchange(URL, HttpMethod.PUT, requestBody, new ParameterizedTypeReference<RESTError>() {}, rideId);	
		
		assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
	}
	
	public void startRide_notFound() {
		final String URL = "/api/ride/{id}/start";
		Long rideId = 0L;
		
		HttpEntity<Void> requestBody = new HttpEntity<Void>(null, getHeader(JWT_DRIVER_2));
		ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.PUT, requestBody, new ParameterizedTypeReference<String>() {}, rideId);	
		
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertEquals("Ride does not exist!", response.getBody());
	}
	
	public void startRide_driverCannotAcceptRideOfOtherDriver() {
		final String URL = "/api/ride/{id}/start";
		Long rideId = 3L;
		
		HttpEntity<Void> requestBody = new HttpEntity<Void>(null, getHeader(JWT_DRIVER_3));
		ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.PUT, requestBody, new ParameterizedTypeReference<String>() {}, rideId);	
		
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertEquals("Ride does not exist!", response.getBody());
	}
	
	public void startRide_notAccepted() {
		final String URL = "/api/ride/{id}/start";
		Long rideId = 1L;
		
		HttpEntity<Void> requestBody = new HttpEntity<Void>(null, getHeader(JWT_DRIVER_BOB));
		ResponseEntity<RESTError> response = restTemplate.exchange(URL, HttpMethod.PUT, requestBody, new ParameterizedTypeReference<RESTError>() {}, rideId);	
		
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("Cannot start a ride that is not in status ACCEPTED!", response.getBody().getMessage());
	}
	
	public void startRide() {
		final String URL = "/api/ride/{id}/start";
		Long rideId = 3L;
		
		HttpEntity<Void> requestBody = new HttpEntity<Void>(null, getHeader(JWT_DRIVER_2));
		ResponseEntity<RideDTO> response = restTemplate.exchange(URL, HttpMethod.PUT, requestBody, new ParameterizedTypeReference<RideDTO>() {}, rideId);	
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		
		RideDTO expected = ride3;
		RideDTO result = response.getBody();
		
		assertThat(result).usingRecursiveComparison().ignoringFields("status", "startTime").isEqualTo(expected);
		assertThat(result.getStatus()).isEqualTo(Status.STARTED);
		assertThat(result.getStartTime()).isNotNull();
		
		// Check if driver is set to not available? No endpoint for that...
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////
}
