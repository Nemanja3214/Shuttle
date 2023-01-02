package com.shuttle.passenger;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.shuttle.ride.Ride;
import com.shuttle.ride.dto.RidePageDTO;
import jakarta.websocket.server.PathParam;

@RestController
public class PassengerController {
    @Autowired
    private IPassengerService passengerService;

	@PostMapping("/api/passenger")
	public ResponseEntity<PassengerDTO> create(@RequestBody Passenger passenger) {
		passenger.setId(Long.valueOf(123));
		return new ResponseEntity<>(new PassengerDTO(passenger), HttpStatus.OK);
	}

	@GetMapping("/api/passenger")
	public ResponseEntity<PassengerPageDTO> getPaginated(@PathParam("page") int page, @PathParam("size") int size) {
		List<Passenger> passengersMock = new ArrayList<>();
		return new ResponseEntity<>(new PassengerPageDTO(passengersMock), HttpStatus.OK);
	}

	@GetMapping("/api/passenger/{id}")
	public ResponseEntity<PassengerDTO> getDetails(@PathVariable("id") Long id) {
		Passenger p = new Passenger();
		return new ResponseEntity<>(new PassengerDTO(p), HttpStatus.OK);
	}

	@GetMapping("/api/passenger/activate/{activationId}")
	public ResponseEntity<Void> activate(@PathVariable("activationId") Long activationId) {
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@PutMapping("/api/passenger/{id}")
	public ResponseEntity<PassengerDTO> update(@RequestBody Passenger newData, @PathVariable("id") Long id) {
		Passenger passengerFromDb = new Passenger();
		return new ResponseEntity<PassengerDTO>(new PassengerDTO(passengerFromDb), HttpStatus.OK);
	}

    
    @GetMapping("/api/passenger/email")
    public ResponseEntity<?> getByEmail(@PathParam("email") String email) {
        Passenger p = passengerService.findByEmail(email);
        if (p == null) {
            return new ResponseEntity<Void>((Void)null, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<PassengerDTO>(new PassengerDTO(p), HttpStatus.OK);
        }
    }

	@GetMapping("/api/passenger/{id}/ride")
	public ResponseEntity<RidePageDTO> getRides(@PathVariable("id") Long passengerId, @PathParam("page") int page,
			@PathParam("size") int size, @PathParam("sort") String sort, @PathParam("from") String from,
			@PathParam("to") String to) {
		List<Ride> ridesMock = new ArrayList<>();
		return new ResponseEntity<RidePageDTO>(new RidePageDTO(ridesMock), HttpStatus.OK);
	}
}
