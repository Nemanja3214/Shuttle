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
import com.shuttle.ride.dto.RideDTO;

@RestController
@RequestMapping("/api/ride")
public class RideController {
	
	@PostMapping
	public ResponseEntity<RideDTO> createRide(@RequestBody CreateRideDTO rideDTO){
		return new ResponseEntity<RideDTO>(new RideDTO(), HttpStatus.OK);
	}
	
	@GetMapping("/driver/{driverId}/active")
	public ResponseEntity<RideDTO> getActiveRideByDriver(@PathVariable long driverId){
		return new ResponseEntity<RideDTO>(new RideDTO(), HttpStatus.OK);
	}
	
	@GetMapping("/passenger/{passengerId}/active")
	public ResponseEntity<RideDTO> getActiveRideByPassenger(@PathVariable long passengerId){
		return new ResponseEntity<RideDTO>(new RideDTO(), HttpStatus.OK);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<RideDTO> getRide(@PathVariable long id) {
		return new ResponseEntity<RideDTO>(new RideDTO(), HttpStatus.OK);
	}
	
	@PutMapping("/{id}/withdraw")
	public ResponseEntity<RideDTO> withdrawRide(@PathVariable long id){
		return new ResponseEntity<RideDTO>(new RideDTO(), HttpStatus.OK);
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
