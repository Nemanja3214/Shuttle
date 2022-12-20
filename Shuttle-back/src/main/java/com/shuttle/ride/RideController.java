package com.shuttle.ride;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shuttle.driver.Driver;
import com.shuttle.driver.IDriverRepository;
import com.shuttle.driver.IDriverService;
import com.shuttle.panic.PanicDTO;
import com.shuttle.ride.dto.CreateRideDTO;
import com.shuttle.ride.dto.RideDTO;

@RestController
@RequestMapping("/api/ride")
public class RideController {
	@Autowired
	private IRideService rideService;
	
	@Autowired
	private IDriverService driverService;
	
	@PostMapping
	public ResponseEntity<RideDTO> createRide(@RequestBody CreateRideDTO rideDTO){
		Ride ride = null;
		try {
			ride = rideService.createRide(rideDTO);
		} catch (NoAvailableDriverException e) {
			System.err.println("Couldn't find driver.");
		}
		return new ResponseEntity<RideDTO>(new RideDTO(ride), HttpStatus.OK);
	}
	
	@GetMapping("/driver/{driverId}/ride-requests")
	public ResponseEntity<RideDTO> getRideRequests(@PathVariable long driverId) {
		final Optional<Driver> odriver = driverService.get(driverId);
		
		if (odriver.isEmpty()) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		} else {
			final Driver driver = odriver.get();
			Optional<Ride> ride = rideService.findPendingRideForDriver(driver);
			
			if (ride.isEmpty()) {
				return new ResponseEntity<>(null, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(new RideDTO(ride.get()), HttpStatus.OK);
			}
		}
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
