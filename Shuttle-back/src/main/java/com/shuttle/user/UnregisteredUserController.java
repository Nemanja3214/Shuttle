package com.shuttle.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shuttle.ride.dto.CreateRideEstimationDTO;
import com.shuttle.ride.dto.EstimationDTO;

@RestController
@RequestMapping("/api/unregisteredUser/")
public class UnregisteredUserController {
	@PostMapping
	public ResponseEntity<EstimationDTO> getEstimatedRide(@RequestBody CreateRideEstimationDTO rideDTO) {
		return new ResponseEntity<EstimationDTO>(EstimationDTO.getMock(), HttpStatus.OK);
	}
}
