package com.shuttle.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shuttle.ride.dto.CreateRideDTO;
import com.shuttle.ride.dto.EstimationDTO;

@RestController
@RequestMapping("/api/unregisteredUser")
public class UnregisteredUserController {
	public ResponseEntity<EstimationDTO> getEstimatedRide(@RequestBody CreateRideDTO rideDTO) {
		return new ResponseEntity<EstimationDTO>( new EstimationDTO(), HttpStatus.OK);
	}
}
