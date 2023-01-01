package com.shuttle.vehicle;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
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
    @Autowired
    private SimpMessagingTemplate template;
	@Autowired
	private IVehicleService vehicleService;

    @Scheduled(fixedDelay = 3000)
    private void notifyVehicleLocations() {
        final String dest = "/vehicle/locations";
        template.convertAndSend(dest, getAllActiveVehicleLocationsDTO());
    }

    private VehicleLocationDTO conv(Vehicle vehicle) {
        VehicleLocationDTO v = new VehicleLocationDTO();
        v.setId(vehicle.getId());
        v.setLocation(LocationDTO.from(vehicle.getCurrentLocation()));
        v.setAvailable(vehicle.getDriver().isAvailable()); // TODO: Active -> Available?
        return v;
    }

	@PutMapping("/{id}/location")
	public ResponseEntity<Boolean>changeLocation(@PathVariable long id, @RequestBody LocationDTO location) {
		return new ResponseEntity<Boolean>(true, HttpStatus.NO_CONTENT);
	}
	
	@PostMapping
	public ResponseEntity<VehicleDTO> createVehicle(@RequestBody VehicleDTO vehicleDTO) {
		Vehicle vehicle = vehicleService.add(vehicleDTO);
		return new ResponseEntity<>(VehicleDTO.from(vehicle), HttpStatus.OK);
	}

    @GetMapping("/locations")
    public ResponseEntity<?> getAllActiveVehicleLocations() {
        return new ResponseEntity<List<VehicleLocationDTO>>(getAllActiveVehicleLocationsDTO(), HttpStatus.OK);
    }

    private List<VehicleLocationDTO> getAllActiveVehicleLocationsDTO() {
        final List<Vehicle> vehicles = this.vehicleService.findAllCurrentlyActive();
        final List<VehicleLocationDTO> result = vehicles.stream().map(v -> conv(v)).toList();
        return result;  
    }
}
