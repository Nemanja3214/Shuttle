package com.shuttle.passenger;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PassengerController {
    @CrossOrigin
    @PostMapping("/api/passenger")
    public ResponseEntity<Passenger> create(@RequestBody Passenger passenger){
        return new ResponseEntity<>(passenger, HttpStatus.OK);
    }
}
