package com.shuttle.passenger;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shuttle.common.ListDTO;
import com.shuttle.common.RESTError;
import com.shuttle.common.exception.NonExistantUserException;
import com.shuttle.common.exception.TokenExpiredException;
import com.shuttle.ride.IRideService;
import com.shuttle.ride.Ride;
import com.shuttle.ride.RideController;
import com.shuttle.ride.dto.RideDTO;
import com.shuttle.ride.dto.RidePassengerDTO;
import com.shuttle.user.GenericUser;
import com.shuttle.user.UserService;
import com.shuttle.user.dto.UserDTONoPassword;
import com.shuttle.user.email.IEmailService;
import com.shuttle.util.MyValidator;
import com.shuttle.util.MyValidatorException;

import jakarta.mail.MessagingException;
import jakarta.websocket.server.PathParam;

@RestController
@RequestMapping("/api/passenger")
public class PassengerController {
	@Autowired
	IEmailService emailService;
	@Autowired
	IPassengerService passengerService;
	@Autowired
	IRideService rideService;
	@Autowired
	UserService userService;
	
	@PostMapping
	public ResponseEntity<?> create(@RequestBody PassengerDTO dto) {
		try {
			MyValidator.validateRequired(dto.getName(), "name");
			MyValidator.validateRequired(dto.getSurname(), "surname");
			MyValidator.validateRequired(dto.getEmail(), "email");
			MyValidator.validateRequired(dto.getAddress(), "address");
			MyValidator.validateRequired(dto.getPassword(), "password");
			
			MyValidator.validateLength(dto.getName(), "name", 100);
			MyValidator.validateLength(dto.getSurname(), "surname", 100);
			MyValidator.validateLength(dto.getTelephoneNumber(), "telephoneNumber", 18);
			MyValidator.validateLength(dto.getEmail(), "email", 100);
			MyValidator.validateLength(dto.getAddress(), "address", 100);
			
			MyValidator.validatePattern(dto.getPassword(), "password", "^(?=.*\\d)(?=.*[A-Z])(?!.*[^a-zA-Z0-9@#$^+=])(.{8,15})$");
		} catch (MyValidatorException e1) {
			return new ResponseEntity<RESTError>(new RESTError(e1.getMessage()), HttpStatus.BAD_REQUEST);
		}
		
		GenericUser userWithThisEmail = userService.findByEmail(dto.getEmail());
		if (userWithThisEmail != null) {
			return new ResponseEntity<RESTError>(new RESTError("User with that email already exists!"), HttpStatus.BAD_REQUEST);
		}
		
		Passenger p = null;
		try {
			p = passengerService.register(dto);
		} catch (IOException e) {
			return new ResponseEntity<RESTError>(new RESTError("Could not save image!"), HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (MessagingException e) {
			return new ResponseEntity<RESTError>(new RESTError("Failed to send verification e-mail!"), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		PassengerDTO result = new PassengerDTO(p);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@GetMapping
	@PreAuthorize("hasAnyAuthority('passenger', 'driver', 'admin')")
	public ResponseEntity<?> getPaginated(Pageable pageable) {
		List<Passenger> passengers = this.passengerService.findAll(pageable);
		List<PassengerDTO> passengersDTO = passengers.stream().map(p -> new PassengerDTO(p)).toList();
		ListDTO<PassengerDTO> result = new ListDTO<>(passengersDTO);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getDetails(@PathVariable("id") Long id) {
		if (id == null) {
            return new ResponseEntity<RESTError>(new RESTError("Field (id) is required!"), HttpStatus.BAD_REQUEST);
        }

        Passenger passenger = passengerService.findById(id);
        if (passenger == null) {
            return new ResponseEntity<>("Passenger does not exist!", HttpStatus.NOT_FOUND);
        }
        
//		final GenericUser user____ = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
//		if (userService.isAdmin(user____) || userService.isDriver(user____)) {	
//		} else {
//	    	if (!passenger.getId().equals(user____.getId())) {
//                return new ResponseEntity<>("User does not exist!", HttpStatus.NOT_FOUND);
//	    	}
//	    }
		
        return new ResponseEntity<>(new UserDTONoPassword(passenger), HttpStatus.OK);
	}

	@GetMapping("/activate/{activationId}")
	public ResponseEntity<?> activate(@PathVariable("activationId") Long activationId) {
		if (activationId == null) {
            return new ResponseEntity<RESTError>(new RESTError("Field (activationId) is required!"), HttpStatus.BAD_REQUEST);
        }
		
		boolean verified = false;
		try {
			verified = passengerService.activate(activationId);
		} catch (TokenExpiredException e) {
			return new ResponseEntity<>(new RESTError( "Activation expired. Register again!"), HttpStatus.BAD_REQUEST);	
		} catch (NonExistantUserException e) {
			return new ResponseEntity<>( "Activation with entered id does not exist!", HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(new RESTError( "Successful account activation!"), HttpStatus.OK);
		
		
//		URI yahoo = null;
//		HttpHeaders httpHeaders = new HttpHeaders();
//	    if (verified) {   	
//			try {
//				yahoo = new URI("http://localhost:4200/login");
//			} catch (URISyntaxException e) {
//				e.printStackTrace();
//			}	    
//		    httpHeaders.setLocation(yahoo);
//		    return new ResponseEntity<>("Successful account activation!", httpHeaders,  HttpStatus.OK);	    
//	    } else {    	
//			try {
//				yahoo = new URI("http://localhost:4200/bad-request");
//			} catch (URISyntaxException e) {
//				e.printStackTrace();
//			}		
//		    httpHeaders.setLocation(yahoo);
//		    return new ResponseEntity<>(httpHeaders, HttpStatus.OK);
//	    }
	}

	@PreAuthorize("hasAnyAuthority('passenger', 'admin')")
	@PutMapping("/{id}")
	public ResponseEntity<?> update(@RequestBody PassengerUpdateDTO newData, @PathVariable("id") Long id) {
		try {
			MyValidator.validateRequired(newData.getName(), "name");
			MyValidator.validateRequired(newData.getSurname(), "surname");
			MyValidator.validateRequired(newData.getEmail(), "email");
			MyValidator.validateRequired(newData.getAddress(), "address");
			
			MyValidator.validateLength(newData.getName(), "name", 100);
			MyValidator.validateLength(newData.getSurname(), "surname", 100);
			MyValidator.validateLength(newData.getTelephoneNumber(), "telephoneNumber", 18);
			MyValidator.validateLength(newData.getEmail(), "email", 100);
			MyValidator.validateLength(newData.getAddress(), "address", 100);
		} catch (MyValidatorException e1) {
			return new ResponseEntity<RESTError>(new RESTError(e1.getMessage()), HttpStatus.BAD_REQUEST);
		}
		
		Passenger updatedPassenger = passengerService.findById(id);
		
		if (updatedPassenger == null) {
			return new ResponseEntity<>("Passenger does not exist!", HttpStatus.NOT_FOUND);
		}
		
		final GenericUser user____ = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		if (userService.isAdmin(user____)) {	
		} else {
	    	if (!updatedPassenger.getId().equals(user____.getId())) {
                return new ResponseEntity<>("User does not exist!", HttpStatus.NOT_FOUND);
	    	}
	    }
		
		try {
			updatedPassenger = this.passengerService.updatePassenger(id, newData);
		} catch (NonExistantUserException e) {
			return new ResponseEntity<>("Passenger does not exist!", HttpStatus.NOT_FOUND);
		} catch (IOException e) {
			return new ResponseEntity<>("Cannot save picture", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(new PassengerDTO(updatedPassenger), HttpStatus.OK);
	}

	@PreAuthorize("hasAnyAuthority('passenger', 'admin')")
	@GetMapping("/{id}/ride")
	public ResponseEntity<?> getRides(@PathVariable Long id, Pageable pageable, @RequestParam(required = false) String from, @RequestParam(required = false) String to) {
		if (id == null) {
			return new ResponseEntity<RESTError>(new RESTError("Field id is required!"), HttpStatus.BAD_REQUEST);
		}
    	
    	LocalDateTime tFrom = null, tTo = null;
    	if (from != null && to != null) {
    		try {
    			DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.of("UTC"));
    			tFrom = LocalDateTime.parse(from, formatter);
    		} catch (DateTimeParseException e) {
    			return new ResponseEntity<RESTError>(new RESTError("Field (from) format is not valid!"), HttpStatus.BAD_REQUEST);
    		}
    		try {
    			DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.of("UTC"));
    			tTo = LocalDateTime.parse(to, formatter);
    		} catch (DateTimeParseException e) {
    			return new ResponseEntity<RESTError>(new RESTError("Field (to) format is not valid!"), HttpStatus.BAD_REQUEST);
    		}
    	}
    	
    	Passenger p = passengerService.findById(id);	
		if (p == null) {
			return new ResponseEntity<>("Passenger does not exist!", HttpStatus.NOT_FOUND);
		}

		final GenericUser user____ = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		if (userService.isAdmin(user____)) {	
		} else {
	    	if (!p.getId().equals(user____.getId())) {
                return new ResponseEntity<>("Passenger does not exist!", HttpStatus.NOT_FOUND);
	    	}
	    }
		
		List<Ride> rides = rideService.findByUser(p, pageable, tFrom, tTo);
		ListDTO<RideDTO> ridesDTO = new ListDTO<>(rides.stream().map(r -> RideController.to(r)).toList());
        return new ResponseEntity<>(ridesDTO, HttpStatus.OK);
	}
	
	////////////////////////
	
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

}
