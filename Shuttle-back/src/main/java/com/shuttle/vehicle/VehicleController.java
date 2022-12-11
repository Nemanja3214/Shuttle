package com.shuttle.vehicle;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shuttle.location.dto.LocationDTO;

@RestController
@RequestMapping("/api/vehicle")
public class VehicleController {

	@PutMapping("/{id}/location")
	public ResponseEntity<Boolean>changeLocation(@PathVariable long id, @RequestBody LocationDTO location) {
		return new ResponseEntity<Boolean>(true, HttpStatus.NO_CONTENT);
	}
}
