package com.shuttle.passenger;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.websocket.server.PathParam;

@RestController
public class PassengerController {
    @PostMapping("/api/passenger")
    public ResponseEntity<PassengerDTO> create(@RequestBody Passenger passenger) {
    	passenger.setId(Long.valueOf(123));
        return new ResponseEntity<>(new PassengerDTO(passenger), HttpStatus.OK);
    }
    
    @GetMapping("/api/passenger")
    public ResponseEntity<PassengerPageDTO> getPaginated(@PathParam("page") int page, @PathParam("size") int size) {
    	List<Passenger> passengersMock = new ArrayList<>();
    	passengersMock.add(new Passenger());
    	passengersMock.add(new Passenger());
    	
        return new ResponseEntity<>(new PassengerPageDTO(passengersMock), HttpStatus.OK);
    }
    
    @GetMapping("/api/passenger/{id}")
    public ResponseEntity<PassengerDTO> getDetails(@PathVariable("id") Long id) {
    	Passenger passenger = new Passenger();
    	passenger.setId(id);
    	
    	return new ResponseEntity<>(new PassengerDTO(passenger), HttpStatus.OK);
    }
    
    @PostMapping("/api/passenger/{activationId}")
    public ResponseEntity<String> activate(@PathVariable("activationId") Long activationId) {
    	return new ResponseEntity<String>("", HttpStatus.OK);
    }
    
    @PutMapping("/api/passenger/{id}")
    public ResponseEntity<PassengerDTO> update(@RequestBody Passenger newData, @PathVariable("id") Long id) {
    	Passenger passengerFromDb = new Passenger();
    	passengerFromDb.setId(id);
    	
    	if (newData.getName() != null) {
    		passengerFromDb.setName(newData.getName());
    	}
    	if (newData.getSurname() != null) {
    		passengerFromDb.setSurname(newData.getSurname());
    	}
    	if (newData.getEmail() != null) {
    		passengerFromDb.setEmail(newData.getEmail());
    	}
    	if (newData.getPassword() != null) {
    		passengerFromDb.setPassword(newData.getPassword());
    	}
    	if (newData.getAddress() != null) {
    		passengerFromDb.setAddress(newData.getAddress());
    	}
    	if (newData.getTelephoneNumber() != null) {
    		passengerFromDb.setTelephoneNumber(newData.getTelephoneNumber());
    	}
    	if (newData.getProfilePicture() != null) {
    		passengerFromDb.setProfilePicture(newData.getProfilePicture());
    	}
    	
    	return new ResponseEntity<PassengerDTO>(new PassengerDTO(passengerFromDb), HttpStatus.OK);
    }
}
