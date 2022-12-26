package com.shuttle.vehicle;

import java.util.List;

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

import com.shuttle.location.dto.LocationDTO;

@RestController
@RequestMapping("/api/vehicle")
public class VehicleController {
	private IVehicleService vehicleService;
	
	@Autowired
	public VehicleController(IVehicleService vehicleService) {
		this.vehicleService = vehicleService;
	}

	@PutMapping("/{id}/location")
	public ResponseEntity<Boolean>changeLocation(@PathVariable long id, @RequestBody LocationDTO location) {
		return new ResponseEntity<Boolean>(true, HttpStatus.NO_CONTENT);
	}
	
	@PostMapping
	public ResponseEntity<VehicleDTO> createVehicle(@RequestBody VehicleDTO vehicleDTO) {
		System.err.println(vehicleDTO.getDriverId());
		
		Vehicle vehicle = vehicleService.add(vehicleDTO);
		return new ResponseEntity<>(VehicleDTO.from(vehicle), HttpStatus.OK);
	}
	
	@GetMapping("/vehicleTypes")
	public ResponseEntity<List<String>> getVehicleTypes(){
		List<String> names = vehicleService.getAllVehicleTypesNames();
		return new ResponseEntity<List<String>>(names, HttpStatus.OK);
	}
}
