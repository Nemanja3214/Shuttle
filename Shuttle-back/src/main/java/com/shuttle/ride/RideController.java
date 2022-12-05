package com.shuttle.ride;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shuttle.panic.PanicDTO;
import com.shuttle.ride.dto.CreateRideDTO;
import com.shuttle.ride.dto.ReadRideDTO;
import com.shuttle.ride.dto.RideDTO;

@RestController
@RequestMapping("/api/ride")
public class RideController {
//	TODO: review especially
	
	@PostMapping
	public ResponseEntity<RideDTO> createRide(@RequestBody CreateRideDTO rideDTO){
		return new ResponseEntity<RideDTO>(new RideDTO(), HttpStatus.OK);
	}
	
	@GetMapping("/active/{driverId}")
	public ResponseEntity<RideDTO> getActiveRideByDriver(@PathVariable long driverId){
		return new ResponseEntity<>(new RideDTO(), HttpStatus.OK);
	}
	
	@GetMapping("/active/{passengerId}")
	public ResponseEntity<RideDTO> getActiveRideByPassenger(@PathVariable long passengerId){
		return new ResponseEntity<>(new RideDTO(), HttpStatus.OK);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ReadRideDTO> getRide(@PathVariable long id) {
		return new ResponseEntity<ReadRideDTO>(new ReadRideDTO(), HttpStatus.OK);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Boolean> cancelRide(@PathVariable long id) {
		return new ResponseEntity<Boolean>(true, HttpStatus.NO_CONTENT);
	}
	
	@PutMapping("/{id}/panic")
	public ResponseEntity<PanicDTO> panicRide(@RequestBody String reason) {
		return new ResponseEntity<PanicDTO>(new PanicDTO(), HttpStatus.OK);
	}
	
	@PutMapping("/{id}/accept")
	public ResponseEntity<RideDTO> acceptRide(@PathVariable long id) {
		return new ResponseEntity<RideDTO>(new RideDTO(), HttpStatus.OK);
	}
	
	@PutMapping("/{id}/end")
	public ResponseEntity<RideDTO> endRide(@PathVariable long id) {
		return new ResponseEntity<RideDTO>(new RideDTO(), HttpStatus.OK);
	}
	
	@PutMapping("/{id}/cancel")
	public ResponseEntity<RideDTO> reasonCancelRide(@PathVariable long id, @RequestBody String reason) {
		return new ResponseEntity<RideDTO>(new RideDTO(), HttpStatus.OK);
	}
	
}
