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
import com.shuttle.ride.Ride;
import com.shuttle.ride.dto.RidePageDTO;
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
		return new ResponseEntity<>(new PassengerPageDTO(passengersMock), HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<PassengerDTO> getDetails(@PathVariable("id") Long id) {
		Passenger p = new Passenger();
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
		return new ResponseEntity<PassengerDTO>(new PassengerDTO(passengerFromDb), HttpStatus.OK);
	}
    
    @GetMapping("/email")
    public ResponseEntity<?> getByEmail(@PathParam("email") String email) {
        Passenger p = passengerService.findByEmail(email);
        if (p == null) {
            return new ResponseEntity<Void>((Void)null, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<PassengerDTO>(new PassengerDTO(p), HttpStatus.OK);
        }
    }

		@GetMapping("/{id}/ride")
	public ResponseEntity<RidePageDTO> getRides(@PathVariable("id") Long passengerId, @PathParam("page") int page,
			@PathParam("size") int size, @PathParam("sort") String sort, @PathParam("from") String from,
			@PathParam("to") String to) {
		List<Ride> ridesMock = new ArrayList<>();
		return new ResponseEntity<RidePageDTO>(new RidePageDTO(ridesMock), HttpStatus.OK);
	}
}
