package com.shuttle.passenger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PassengerController {

    @PostMapping("/api/passenger")
    public ResponseEntity<Passenger> create(@RequestBody Passenger passenger) {
    	passenger.setId(Long.valueOf(123));
        return new ResponseEntity<>(passenger, HttpStatus.OK);
    }
}
