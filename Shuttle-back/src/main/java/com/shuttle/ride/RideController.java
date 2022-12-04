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

import com.shuttle.ride.dto.AcceptRideDTO;
import com.shuttle.ride.dto.CancelRideDTO;
import com.shuttle.ride.dto.CreateRideDTO;
import com.shuttle.ride.dto.EndRideDTO;
import com.shuttle.ride.dto.ReadRideDTO;
import com.shuttle.ride.dto.RejectionDTO;
import com.shuttle.ride.dto.RideDTO;

@RestController
@RequestMapping("/api/ride")
public class RideController {
	
	@PostMapping
	public ResponseEntity<CreateRideDTO> createRide(@RequestBody RideDTO rideDTO){
		return new ResponseEntity<CreateRideDTO>(new CreateRideDTO(), HttpStatus.OK);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ReadRideDTO> getRide(@PathVariable long id) {
		return new ResponseEntity<ReadRideDTO>(new ReadRideDTO(), HttpStatus.OK);
	}
	
//	shouldn't here also be as reason?
	@PutMapping("/{id}")
	public ResponseEntity<Boolean> cancelRide(@PathVariable long id) {
		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}
	
	@PutMapping("/{id}/panic")
	public ResponseEntity<RejectionDTO> panicRide(@RequestBody String reason) {
		return new ResponseEntity<RejectionDTO>(new RejectionDTO(), HttpStatus.OK);
	}
	
	@PutMapping("/{id}/accept")
	public ResponseEntity<AcceptRideDTO> acceptRide(@PathVariable long id) {
		return new ResponseEntity<AcceptRideDTO>(new AcceptRideDTO(), HttpStatus.OK);
	}
	
	@PutMapping("/{id}/end")
	public ResponseEntity<EndRideDTO> endRide(@PathVariable long id) {
		return new ResponseEntity<EndRideDTO>(new EndRideDTO(), HttpStatus.OK);
	}
	
	@PutMapping("/{id}/cancel")
	public ResponseEntity<CancelRideDTO> reasonCancelRide(@PathVariable long id, @RequestBody String reason) {
		return new ResponseEntity<CancelRideDTO>(new CancelRideDTO(), HttpStatus.OK);
	}
	
}
