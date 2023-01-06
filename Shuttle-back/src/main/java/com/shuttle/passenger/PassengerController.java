package com.shuttle.passenger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shuttle.common.exception.EmailAlreadyUsedException;
import com.shuttle.common.exception.NonExistantUserException;
import com.shuttle.common.exception.TokenExpiredException;
import com.shuttle.ride.Ride;
import com.shuttle.ride.dto.RidePageDTO;
import com.shuttle.user.email.IEmailService;

import jakarta.mail.MessagingException;
import jakarta.websocket.server.PathParam;

class Desc{
	public String desc;
	public String title;
}

@RestController
@RequestMapping("/api/passenger")
public class PassengerController {
	@Autowired
	IEmailService emailService;
	
	@Autowired
	IPassengerService passengerService;
	
	@PostMapping
	public ResponseEntity<?> create(@RequestBody PassengerDTO dto) {
		if(dto.isInvalid()) {
			return ResponseEntity.badRequest().body("Invalid user data sent");
		}
		
		try {
			dto = passengerService.register(dto);
		} catch (UnsupportedEncodingException e) {
			return ResponseEntity.badRequest().body("Bad encoding");
		} catch (MessagingException e) {
			return ResponseEntity.badRequest().body("Failed to send mail");
		} catch (EmailAlreadyUsedException e) {
			return ResponseEntity.badRequest().body("Email is already used");
		} catch (IOException e) {
			return ResponseEntity.internalServerError().body("Couldn't save image, using default image instead");
		}
		return new ResponseEntity<>(dto, HttpStatus.OK);
	}

	@GetMapping("/verify")
	public ResponseEntity<?> verifyUser(@RequestParam("token") String code) {
		boolean verified = false;
		try {
			verified = passengerService.verify(code);
		} catch (TokenExpiredException e) {
			return new ResponseEntity<>("Activation expired. Register again!", HttpStatus.BAD_REQUEST);	
		} catch (NonExistantUserException e) {
			return new ResponseEntity<>("Activation with entered id does not exist!", HttpStatus.NOT_FOUND);
		}
		
		URI yahoo = null;
		HttpHeaders httpHeaders = new HttpHeaders();
	    if (verified) {
	    	
			try {
				yahoo = new URI("http://localhost:4200/login");
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		    
		    httpHeaders.setLocation(yahoo);
		    return new ResponseEntity<>(httpHeaders, HttpStatus.OK);
		    
	    } else {
	    	
			try {
				yahoo = new URI("http://localhost:4200/bad-request");
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			
		    httpHeaders.setLocation(yahoo);
		    return new ResponseEntity<>(httpHeaders, HttpStatus.NOT_ACCEPTABLE);
	    }
	}

	@GetMapping
	public ResponseEntity<PassengerPageDTO> getPaginated(@PathParam("page") int page, @PathParam("size") int size) {
		List<Passenger> passengersMock = new ArrayList<>();
//		Passenger p = new Passenger();
//		p.setId(Long.valueOf(0));
//		p.setAddress("ASBABS");
//		p.setEmail("haksjah");
//		p.setName("SJKAHS");
//		p.setSurname("ahsjka");
//		p.setPassword("hdjk");
//		p.setTelephoneNumber("hdkwdhswkjdhsjk");
//		p.setProfilePicture("hjksfhfkrjefyewiuf4yur983hf==");
//		passengersMock.add(p);

		return new ResponseEntity<>(new PassengerPageDTO(passengersMock), HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<PassengerDTO> getDetails(@PathVariable("id") Long id) {
		Passenger p = new Passenger();
//		p.setId(Long.valueOf(0));
//		p.setAddress("ASBABS");
//		p.setEmail("haksjah");
//		p.setName("SJKAHS");
//		p.setSurname("ahsjka");
//		p.setPassword("hdjk");
//		p.setTelephoneNumber("hdkwdhswkjdhsjk");
//		p.setProfilePicture("hjksfhfkrjefyewiuf4yur983hf==");

		return new ResponseEntity<>(new PassengerDTO(p), HttpStatus.OK);
	}

	@GetMapping("/activate/{activationId}")
	public ResponseEntity<?> activate(@PathVariable("activationId") Long activationId) {
		boolean verified = false;
		try {
			verified = passengerService.activate(activationId);
		} catch (TokenExpiredException e) {
			return new ResponseEntity<>("Activation expired. Register again!", HttpStatus.BAD_REQUEST);	
		} catch (NonExistantUserException e) {
			return new ResponseEntity<>("Activation with entered id does not exist!", HttpStatus.NOT_FOUND);
		}
		
		URI yahoo = null;
		HttpHeaders httpHeaders = new HttpHeaders();
	    if (verified) {
	    	
			try {
				yahoo = new URI("http://localhost:4200/login");
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		    
		    httpHeaders.setLocation(yahoo);
		    return new ResponseEntity<>("Successful account activation!", httpHeaders,  HttpStatus.OK);
		    
	    } else {
	    	
			try {
				yahoo = new URI("http://localhost:4200/bad-request");
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			
		    httpHeaders.setLocation(yahoo);
		    return new ResponseEntity<>(httpHeaders, HttpStatus.NOT_ACCEPTABLE);
	    }
	}

	@PutMapping("/{id}")
	public ResponseEntity<PassengerDTO> update(@RequestBody Passenger newData, @PathVariable("id") Long id) {
		Passenger passengerFromDb = new Passenger();
//		passengerFromDb.setId(id);
//
//		if (newData.getName() != null) {
//			passengerFromDb.setName(newData.getName());
//		}
//		if (newData.getSurname() != null) {
//			passengerFromDb.setSurname(newData.getSurname());
//		}
//		if (newData.getEmail() != null) {
//			passengerFromDb.setEmail(newData.getEmail());
//		}
//		if (newData.getPassword() != null) {
//			passengerFromDb.setPassword(newData.getPassword());
//		}
//		if (newData.getAddress() != null) {
//			passengerFromDb.setAddress(newData.getAddress());
//		}
//		if (newData.getTelephoneNumber() != null) {
//			passengerFromDb.setTelephoneNumber(newData.getTelephoneNumber());
//		}
//		if (newData.getProfilePicture() != null) {
//			passengerFromDb.setProfilePicture(newData.getProfilePicture());
//		}

		return new ResponseEntity<PassengerDTO>(new PassengerDTO(passengerFromDb), HttpStatus.OK);
	}

	@GetMapping("/{id}/ride")
	public ResponseEntity<RidePageDTO> getRides(@PathVariable("id") Long passengerId, @PathParam("page") int page,
			@PathParam("size") int size, @PathParam("sort") String sort, @PathParam("from") String from,
			@PathParam("to") String to) {
		List<Ride> ridesMock = new ArrayList<>();
//		Ride r = new Ride();
//		r.setDriver(new Driver());
//
//		r.setId(Long.valueOf(43798));
//
//		r.getDriver().setId(Long.valueOf(0));
//		r.getDriver().setAddress("ASBABS");
//		r.getDriver().setEmail("haksjah");
//		r.getDriver().setName("SJKAHS");
//		r.getDriver().setSurname("ahsjka");
//		r.getDriver().setPassword("hdjk");
//		r.getDriver().setTelephoneNumber("hdkwdhswkjdhsjk");
//		r.getDriver().setProfilePicture("hjksfhfkrjefyewiuf4yur983hf==");
//
//		r.setPassengers(new HashSet<>());
//
//		Passenger p = new Passenger();
//		p.setId(Long.valueOf(0));
//		p.setAddress("ASBABS");
//		p.setEmail("haksjah");
//		p.setName("SJKAHS");
//		p.setSurname("ahsjka");
//		p.setPassword("hdjk");
//		p.setTelephoneNumber("hdkwdhswkjdhsjk");
//		p.setProfilePicture("hjksfhfkrjefyewiuf4yur983hf==");
//		r.getPassengers().add(p);
//
//		Location l = new Location();
//		l.setLatitude(23.32);
//		l.setLongitude(32.23);
//		l.setAddress("hfdkjdfhkdsj");
//
//		// We need two (specifically, an even number) to build RidePageDTO and it's a set so no duplicates.
//		Location l2 = new Location();
//		l2.setLatitude(23.32);
//		l2.setLongitude(542.23);
//		l2.setAddress("ds");
//
//		r.setLocations(new HashSet<>());
//		r.getLocations().add(l);
//		r.getLocations().add(l2);
//
//
//		r.setStartTime(LocalDateTime.now());
//		r.setEndTime(LocalDateTime.now());
//		r.setTotalCost(13902);
//		r.setBabyTransport(false);
//		r.setPetTransport(false);
//		r.setEstimatedTimeInMinutes(12);
//		r.setVehicleType(Type.STANDARD);
//
//		ridesMock.add(r);

		return new ResponseEntity<RidePageDTO>(new RidePageDTO(ridesMock), HttpStatus.OK);
	}
}
