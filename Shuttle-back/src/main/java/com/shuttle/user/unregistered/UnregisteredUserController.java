package com.shuttle.user.unregistered;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shuttle.common.RESTError;
import com.shuttle.ride.dto.CreateRideEstimationDTO;
import com.shuttle.ride.dto.EstimationDTO;

@RestController
@RequestMapping("/api/unregisteredUser")
public class UnregisteredUserController {
	@Autowired
	private IUnregisteredUserService unregiteredUserService;
	
	@PostMapping
	public ResponseEntity<?> getEstimatedRide(@RequestBody CreateRideEstimationDTO rideDTO) {
		EstimationDTO estimation = unregiteredUserService.getEstimation(rideDTO);
		
		if (estimation == null) {
			return new ResponseEntity<RESTError>(new RESTError("Could not create estimation."), HttpStatus.BAD_REQUEST);
		}
			
		return new ResponseEntity<EstimationDTO>(estimation, HttpStatus.OK);
	}
}
