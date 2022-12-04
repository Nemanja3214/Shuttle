package com.shuttle.user;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shuttle.ride.dto.CreateRideDTO;
import com.shuttle.ride.dto.EstimationDTO;

@RestController
@RequestMapping("unregisteredUser")
public class UnregisteredUserController {
	public EstimationDTO getEstimatedRide(@RequestBody CreateRideDTO rideDTO) {
		return new EstimationDTO();
	}
}
