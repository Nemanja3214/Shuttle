package com.shuttle.passenger;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.websocket.server.PathParam;

@RestController
public class PassengerController {
    @CrossOrigin
    @PostMapping("/api/passenger")
    public ResponseEntity<PassengerDTO> create(@RequestBody Passenger passenger) {
    	passenger.setId(Long.valueOf(123));
        return new ResponseEntity<>(new PassengerDTO(passenger), HttpStatus.OK);
    }
    
    @CrossOrigin
    @GetMapping("/api/passenger")
    public ResponseEntity<PassengerPageDTO> getPaginated(@PathParam("page") int page, @PathParam("size") int size) {
    	List<Passenger> passengersMock = new ArrayList<>();
    	passengersMock.add(new Passenger());
    	passengersMock.add(new Passenger());
    	
        return new ResponseEntity<>(new PassengerPageDTO(passengersMock), HttpStatus.OK);
    }    
}
