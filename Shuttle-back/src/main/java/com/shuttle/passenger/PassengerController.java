package com.shuttle.passenger;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

import com.shuttle.common.ListDTO;
import com.shuttle.common.exception.EmailAlreadyUsedException;
import com.shuttle.common.exception.NonExistantUserException;
import com.shuttle.common.exception.TokenExpiredException;
import com.shuttle.ride.IRideService;
import com.shuttle.ride.Ride;
import com.shuttle.ride.dto.RideDTO;
import com.shuttle.ride.dto.RidePassengerDTO;
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

	@Autowired
	IRideService rideService;
	
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
	public ResponseEntity<?>getPaginated(@PathParam("page") int page, @PathParam("size") int size) {
		Pageable pageable = PageRequest.of(page, size);
		List<Passenger> passengers = this.passengerService.findAll(pageable);
		List<PassengerDTO> passengersDTO = passengers.stream().map(p -> new PassengerDTO(p)).toList();
		ListDTO<PassengerDTO> result = new ListDTO<>(passengersDTO);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getDetails(@PathVariable("id") Long id) {
		Passenger passenger = this.passengerService.findById(id);
		
		if(passenger == null) {
			return new ResponseEntity<>("Passenger does not exist!", HttpStatus.NOT_FOUND);
		}
		else {
			return new ResponseEntity<>(new PassengerDTO(passenger), HttpStatus.OK);
		}
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
	public ResponseEntity<?> update(@RequestBody PassengerUpdateDTO newData, @PathVariable("id") Long id) {
		Passenger updatedPassenger;
		try {
			updatedPassenger = this.passengerService.updatePassenger(id, newData);
		} catch (NonExistantUserException e) {
			return new ResponseEntity<>("Passenger does not exist!", HttpStatus.NOT_FOUND);
		} catch (IOException e) {
			return new ResponseEntity<>("Cannot save picture", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(new PassengerDTO(updatedPassenger), HttpStatus.OK);
	}
    
    @GetMapping("/email")
    public ResponseEntity<?> getByEmail(@PathParam("email") String email) {
        Passenger p = passengerService.findByEmail(email);
        if (p == null) {
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        } else {
        	RidePassengerDTO dto = new RidePassengerDTO(p);
            return new ResponseEntity<RidePassengerDTO>(dto, HttpStatus.OK);
        }
    }

	@GetMapping("/{id}/ride")
	public ResponseEntity<?> getRides(@PathVariable("id") Long passengerId, @PathParam("page") int page,
			@PathParam("size") int size, @PathParam("sort") String sort, @PathParam("from") String from,
			@PathParam("to") String to) {
		
		Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
		List<Ride> rides;
		try {
			rides = this.rideService.findRidesByPassengerInDateRange(passengerId, from, to, pageable);
			List<RideDTO> ridesDTO = rides.stream().map(ride -> new RideDTO(ride)).toList();
			return new ResponseEntity<>(new ListDTO<RideDTO>(ridesDTO), HttpStatus.OK);
		} catch (NonExistantUserException e) {
			return new ResponseEntity<>("Passenger does not exist!", HttpStatus.NOT_FOUND);
		}
		
	}
}
