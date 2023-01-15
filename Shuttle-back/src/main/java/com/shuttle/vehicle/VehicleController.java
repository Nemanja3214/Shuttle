package com.shuttle.vehicle;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shuttle.common.RESTError;
import com.shuttle.driver.Driver;
import com.shuttle.location.Location;
import com.shuttle.location.dto.LocationDTO;
import com.shuttle.passenger.Passenger;
import com.shuttle.ride.IRideService;
import com.shuttle.ride.Ride;
import com.shuttle.ride.Ride.Status;
import com.shuttle.user.GenericUser;
import com.shuttle.user.UserService;
import com.shuttle.util.MyValidator;
import com.shuttle.util.MyValidatorException;
import com.shuttle.vehicle.vehicleType.VehicleTypeDTO;

@RestController
@RequestMapping("/api/vehicle")
public class VehicleController {
    @Autowired
    private SimpMessagingTemplate template;
	@Autowired
	private IVehicleService vehicleService;
    @Autowired
    private IRideService rideService;
    @Autowired
    private UserService userService; 

    /**
     * Notify users (unregistered user home page + passenger home page) of the drivers' locations
     * and availability.
     */
    @Scheduled(fixedDelay = 2000)
    private void notifyVehicleLocations() {
        final String dest = "/vehicle/locations";
        template.convertAndSend(dest, getAllActiveVehicleLocationsDTO());
    }

    /**
     * Notify each driver about his vehicle location and availability.
     */
    @Scheduled(fixedDelay = 3000)
    private void notifyVehicleLocation() {
        for (Vehicle v : getAllActiveVehicles()) {
            final String dest = "/vehicle/locations/" + v.getDriver().getId();
            template.convertAndSend(dest, conv(v));
        }
    }

    private void notifyPassengersVehicleArrived(Ride r) {
        for (Passenger p : r.getPassengers()) {
            final String dest = "/vehicle/arrived/" + p.getId();
            template.convertAndSend(dest, "true");
        }
    }

    @Scheduled(fixedDelay = 2000)
    private void vehicleMovementSimulation() {
        // TODO: Batch update.

        for (Vehicle v : vehicleService.findAllCurrentlyActiveWhoseDriverCanWork()) {
            if (v.getDriver().isAvailable()) {
                // It doesn't make sense for a free car to roam around?
            } else {
                final Ride r = rideService.findCurrentRideByDriver(v.getDriver());
                if (r == null) {
                    throw new IllegalStateException("Driver is ACTIVE and NOT AVAILABLE => He MUST have a ride");
                }

                Location target = r.getLocations().get(0);
                if (r.getStatus() == Status.Accepted || r.getStatus() == Status.Started) {
                    target = r.getLocations().get(r.getLocations().size() - 1);
                }

                final Double x2 = target.getLongitude();
                final Double y2 = target.getLatitude();
                final Double currentX1 = v.getCurrentLocation().getLongitude();
                final Double currentY1 = v.getCurrentLocation().getLatitude();
                Double x1 = currentX1;
                Double y1 = currentY1;
                final Double dx = 60.0 * 0.00003;
                final Double dy = 60.0 * 0.00003;

  
                x1 += dx * Math.signum(x2 - x1);
                y1 += dy * Math.signum(y2 - y1);

                if (Math.abs(x2 - x1) <= dx) {
                    x1 = x2;
                }
                if (Math.abs(y2 - y1) <= dy) {
                    y1 = y2;
                }

                if (Math.abs(x2 - x1) <= dx
                && Math.abs(y2 - y1) <= dy
                && (Math.abs(currentX1 - x1) > dx
                || Math.abs(currentY1 - y1) > dy)) {
                    notifyPassengersVehicleArrived(r);
                }

                v.getCurrentLocation().setLongitude(x1);
                v.getCurrentLocation().setLatitude(y1);
            }

            vehicleService.add(v); // This will just update the vehicle in the database.
        }
    }


    private VehicleLocationDTO conv(Vehicle vehicle) {
        VehicleLocationDTO v = new VehicleLocationDTO();
        v.setId(vehicle.getId());
        v.setLocation(LocationDTO.from(vehicle.getCurrentLocation()));
        v.setAvailable(vehicle.getDriver().isAvailable());
        v.setVehicleTypeId(vehicle.getVehicleType().getId());
        return v;
    }

	@PutMapping("/{id}/location")
	public ResponseEntity<?> changeLocation(@PathVariable Long id, @RequestBody LocationDTO location) {
		try {
			MyValidator.validateRequired(location.getLatitude(), "latitude");
			MyValidator.validateRequired(location.getLongitude(), "longitude");
			
			MyValidator.validateRange(location.getLatitude().longValue(), "latitude", -90L, 90L);
			MyValidator.validateRange(location.getLongitude().longValue(), "longitude", -90L, 90L);
		} catch (MyValidatorException e1) {
			return new ResponseEntity<RESTError>(new RESTError(e1.getMessage()), HttpStatus.BAD_REQUEST);
		}	
		
		if (id == null) {
            return new ResponseEntity<RESTError>(new RESTError("Bad ID format."), HttpStatus.BAD_REQUEST);
        }
		
		Vehicle v = vehicleService.findById(id);
    	
		if (v == null) {
			return new ResponseEntity<>("Vehicle does not exist!", HttpStatus.NOT_FOUND);
		}
		
		this.vehicleService.changeCurrentLocation(id, location);
		
		final GenericUser user = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		if (userService.isDriver(user)) {
			if (user.getId() != v.getDriver().getId()) {
				return new ResponseEntity<>("Vehicle does not exist!", HttpStatus.NOT_FOUND);
			}
		}

    	return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}
	
	@GetMapping("/android/active")
    public ResponseEntity<List<VehicleLocationDTO>> getAllActiveVehicleLocations() {
		final List<Vehicle> vehicles = vehicleService.findAllCurrentlyActive();
		final List<VehicleLocationDTO> result = vehicles.stream().map(v -> conv(v)).toList();
        return new ResponseEntity<>( result, HttpStatus.OK);
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
        final List<Vehicle> vehicles = this.vehicleService.findAllCurrentlyActiveWhoseDriverCanWork();
        final List<VehicleLocationDTO> result = vehicles.stream().map(v -> conv(v)).toList();
        return result;  
    }

    /**
     * @return List of all active vehicles.
     */
    private List<Vehicle> getAllActiveVehicles() {
        final List<Vehicle> vehicles = this.vehicleService.findAllCurrentlyActiveWhoseDriverCanWork();
        return vehicles;  
    }
	
	@GetMapping("/vehicleTypes")
	public ResponseEntity<List<String>> getVehicleTypes(){
		List<String> names = vehicleService.getAllVehicleTypesNames();
		return new ResponseEntity<List<String>>(names, HttpStatus.OK);
	}
	
	@GetMapping("/types")
	public ResponseEntity<List<VehicleTypeDTO>> getAllVehicleTypes() {
		List<VehicleTypeDTO> result = vehicleService.getAllVehicleTypes().stream().map(vt -> {
			return new VehicleTypeDTO(vt.getId(), vt.getName(), vt.getPricePerKM());
		}).toList();		
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
}
