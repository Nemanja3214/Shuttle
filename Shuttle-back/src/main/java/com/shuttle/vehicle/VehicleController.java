package com.shuttle.vehicle;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
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

import com.shuttle.location.Location;
import com.shuttle.location.dto.LocationDTO;
import com.shuttle.ride.IRideService;
import com.shuttle.ride.Ride;
import com.shuttle.ride.Ride.Status;

@RestController
@RequestMapping("/api/vehicle")
public class VehicleController {
    @Autowired
    private SimpMessagingTemplate template;
	@Autowired
	private IVehicleService vehicleService;
    @Autowired
    private IRideService rideService;

    /**
     * Notify users (unregistered user home page + passenger home page) of the drivers' locations
     * and availability.
     */
    @Scheduled(fixedDelay = 1000)
    private void notifyVehicleLocations() {
        final String dest = "/vehicle/locations";
        template.convertAndSend(dest, getAllActiveVehicleLocationsDTO());
    }

    @Scheduled(fixedDelay = 1000)
    private void vehicleMovementSimulation() {
        // TODO: Batch update.

        for (Vehicle v : vehicleService.findAllCurrentlyActive()) {
            if (v.getDriver().isAvailable()) {
                // It doesn't make sense for a free car to roam around?
            } else {
                final Ride r = rideService.findCurrentRideByDriver(v.getDriver());
                if (r == null) {
                    throw new IllegalStateException("Driver is ACTIVE and NOT AVAILABLE => He MUST have a ride");
                }

                Location target = r.getLocations().get(0);
                if (r.getStatus() == Status.Accepted) {
                    target = r.getLocations().get(r.getLocations().size() - 1);
                }

                final Double x2 = target.getLongitude();
                final Double y2 = target.getLatitude();
                final Double x1 = v.getCurrentLocation().getLongitude();
                final Double y1 = v.getCurrentLocation().getLatitude();
                final Double dx = (x2 - x1) * 0.2;
                final Double dy = (y2 - y1) * 0.2;
                v.getCurrentLocation().setLongitude(x1 + dx);
                v.getCurrentLocation().setLatitude(y1 + dy);
            }

            vehicleService.add(v); // This will just update the vehicle in the database.
        }
    }


    private VehicleLocationDTO conv(Vehicle vehicle) {
        VehicleLocationDTO v = new VehicleLocationDTO();
        v.setId(vehicle.getId());
        v.setLocation(LocationDTO.from(vehicle.getCurrentLocation()));
        v.setAvailable(vehicle.getDriver().isAvailable());
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

    /**
     * @return List of all active vehicle's id + locations + availability.
     */
    private List<VehicleLocationDTO> getAllActiveVehicleLocationsDTO() {
        final List<Vehicle> vehicles = this.vehicleService.findAllCurrentlyActive();
        final List<VehicleLocationDTO> result = vehicles.stream().map(v -> conv(v)).toList();
        return result;  
    }
}
