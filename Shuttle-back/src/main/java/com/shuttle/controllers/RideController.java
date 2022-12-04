package com.shuttle.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shuttle.ride.dto.AcceptRideDTO;
import com.shuttle.ride.dto.CancelRideDTO;
import com.shuttle.ride.dto.CreateRideDTO;
import com.shuttle.ride.dto.EndRideDTO;
import com.shuttle.ride.dto.ReadRideDTO;
import com.shuttle.ride.dto.RejectionDTO;
import com.shuttle.ride.dto.RideDTO;

@RestController
@RequestMapping("/ride")
public class RideController {
	
	@PostMapping
	public CreateRideDTO createRide(@RequestBody RideDTO rideDTO){
		return new CreateRideDTO();
	}
	
	@GetMapping("/{id}")
	public ReadRideDTO getRide(@PathVariable long id) {
		return new ReadRideDTO();
	}
	
//	shouldn't here also be as reason?
	@PutMapping("/{id}")
	public boolean cancelRide(@PathVariable long id) {
		return true;
	}
	
	@PutMapping("/{id}/panic")
	public RejectionDTO panicRide(@RequestBody String reason) {
		return new RejectionDTO();
	}
	
	@PutMapping("/{id}/accept")
	public AcceptRideDTO acceptRide(@PathVariable long id) {
		return new AcceptRideDTO();
	}
	
	@PutMapping("/{id}/end")
	public EndRideDTO endRide(@PathVariable long id) {
		return new EndRideDTO();
	}
	
	@PutMapping("/{id}/cancel")
	public CancelRideDTO reasonCancelRide(@PathVariable long id, @RequestBody String reason) {
		return new CancelRideDTO();
	}
	
}
