package com.shuttle.user.unregistered;

import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shuttle.common.RESTError;
import com.shuttle.common.exception.NonExistantVehicleType;
import com.shuttle.ride.dto.CreateRideEstimationDTO;
import com.shuttle.ride.dto.EstimationDTO;
import com.shuttle.util.MyValidator;
import com.shuttle.util.MyValidatorException;

@RestController
@RequestMapping("/api/unregisteredUser")
public class UnregisteredUserController {
	@Autowired
	private IUnregisteredUserService unregiteredUserService;

	@PermitAll
	@PostMapping
	public ResponseEntity<?> getEstimatedRide(@RequestBody CreateRideEstimationDTO rideDTO) {
		try {
			MyValidator.validateRequired(rideDTO.getLocations(), "locations");
			MyValidator.validateRequired(rideDTO.getVehicleType(), "vehicleType");
			MyValidator.validateRequired(rideDTO.getBabyTransport(), "babyTransport");
			MyValidator.validateRequired(rideDTO.getBabyTransport(), "petTransport");
			
			MyValidator.validateRouteDTO(rideDTO.getLocations(), "locations");
			MyValidator.validateLength(rideDTO.getVehicleType(), "vehicleType", 50);
		} catch (MyValidatorException e1) {
			return new ResponseEntity<RESTError>(new RESTError(e1.getMessage()), HttpStatus.BAD_REQUEST);
		}	
		
		EstimationDTO estimation;
		try {
			estimation = unregiteredUserService.getEstimation(rideDTO);
		} catch (NonExistantVehicleType e) {
			return new ResponseEntity<>("Vehicle type does not exist!", HttpStatus.NOT_FOUND);
		}
		
		if (estimation == null) {
			return new ResponseEntity<RESTError>(new RESTError("Could not create estimation."), HttpStatus.BAD_REQUEST);
		}
			
		return new ResponseEntity<EstimationDTO>(estimation, HttpStatus.OK);
	}
}
