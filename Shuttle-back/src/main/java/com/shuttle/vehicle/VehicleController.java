package com.shuttle.vehicle;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shuttle.location.dto.LocationDTO;

@RestController
@RequestMapping("/vehicle")
public class VehicleController {

	@PutMapping
	@RequestMapping("/{id}/location")
	public boolean changeLocation(@PathVariable long id, @RequestBody LocationDTO location) {
		return true;
	}
}
