package com.shuttle.ride;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

import com.shuttle.driver.Driver;
import com.shuttle.driver.IDriverRepository;
import com.shuttle.driver.IDriverService;
import com.shuttle.location.ILocationService;
import com.shuttle.location.Location;
import com.shuttle.location.Route;
import com.shuttle.location.dto.LocationDTO;
import com.shuttle.location.dto.RouteDTO;
import com.shuttle.panic.PanicDTO;
import com.shuttle.passenger.IPassengerRepository;
import com.shuttle.passenger.Passenger;
import com.shuttle.ride.Ride.Status;
import com.shuttle.ride.dto.CreateRideDTO;
import com.shuttle.ride.dto.RideDTO;
import com.shuttle.vehicle.IVehicleRepository;
import com.shuttle.vehicle.IVehicleTypeRepository;
import com.shuttle.vehicle.Vehicle;
import com.shuttle.vehicle.VehicleType;

@RestController
@RequestMapping("/api/ride")
public class RideController {
	@Autowired
	private IRideService rideService;
	@Autowired
	private IDriverService driverService;
	@Autowired
	private IVehicleTypeRepository vehicleTypeRepository;
	@Autowired
	private IVehicleRepository vehicleRepository;
	@Autowired
	private IPassengerRepository passengerRepository;
	@Autowired
	private ILocationService locationService;
	
	public Ride from(CreateRideDTO rideDTO, Driver driver) {
		final Double distance = 0.5; // TODO: Where to get total distance from?
		final Double velocity = 30.0 / 1000.0; // TODO: Where to get average vehicle velocity from?
		
		final VehicleType vehicleType = vehicleTypeRepository.findVehicleTypeByName(rideDTO.getVehicleType()).orElseThrow();
		final Double cost = (vehicleType.getPricePerKM() + 120) * distance;
		final Vehicle vehicle = vehicleRepository.findByDriver(driver);
		assert(vehicle.getVehicleType().getName().equals(vehicleType.getName()));
		
		final Set<Passenger> passengers = rideDTO.getPassengers()
				.stream()
				.map(userInfo -> passengerRepository.findByEmail(userInfo.getEmail()))
				.collect(Collectors.toSet());	
		
		final List<RouteDTO> routeDTO = rideDTO.getLocations();
		
		// .stream().toList() returns an *immutable* list, hence the: new ArrayList<...>().
		
		final List<LocationDTO> locationsDTO = new ArrayList<LocationDTO>(routeDTO.stream().map(rou -> rou.getDeparture()).toList());
		locationsDTO.add(routeDTO.get(routeDTO.size() - 1).getDestination());	
		final List<Location> locations = locationsDTO.stream().map(loc -> locationService.findOrAdd(loc)).toList();
		
		Route route = new Route();
		route.setLocations(locations);
		
		Ride r = new Ride();		
		r.setStatus(Status.Pending);
		r.setTotalCost(cost);
		r.setDriver(driver);
		r.setVehicleType(vehicleType);
		r.setBabyTransport(rideDTO.isBabyTransport());
		r.setPetTransport(rideDTO.isPetTransport());
		r.setEstimatedTimeInMinutes((int)((distance / velocity) * 60));
		r.setPassengers(passengers);
		r.setRoute(route);
		
		return r;
	}
	
	@PostMapping
	public ResponseEntity<RideDTO> createRide(@RequestBody CreateRideDTO createRideDTO){
		try {
			final Driver driver = rideService.findMostSuitableDriver(createRideDTO);
			final Ride ride = from(createRideDTO, driver);
			rideService.createRide(ride);
			return new ResponseEntity<RideDTO>(new RideDTO(ride), HttpStatus.OK);
		} catch (NoAvailableDriverException e1) {
			System.err.println("Couldn't find driver.");
			return new ResponseEntity<RideDTO>(new RideDTO(null), HttpStatus.OK);
		}
	}
	
	@GetMapping("/driver/{driverId}/ride-requests")
	public ResponseEntity<RideDTO> getRideRequests(@PathVariable long driverId) {
		final Optional<Driver> odriver = driverService.get(driverId);
		
		if (odriver.isEmpty()) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		} else {
			final Driver driver = odriver.get();
			Optional<Ride> ride = rideService.findPendingRideForDriver(driver);
			
			if (ride.isEmpty()) {
				return new ResponseEntity<>(null, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(new RideDTO(ride.get()), HttpStatus.OK);
			}
		}
	}
	
	@GetMapping("/driver/{driverId}/active")
	public ResponseEntity<RideDTO> getActiveRideByDriver(@PathVariable long driverId){
		return new ResponseEntity<RideDTO>(new RideDTO(), HttpStatus.OK);
	}
	
	@GetMapping("/passenger/{passengerId}/active")
	public ResponseEntity<RideDTO> getActiveRideByPassenger(@PathVariable long passengerId){
		return new ResponseEntity<RideDTO>(new RideDTO(), HttpStatus.OK);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<RideDTO> getRide(@PathVariable long id) {
		return new ResponseEntity<RideDTO>(new RideDTO(), HttpStatus.OK);
	}
	
	@PutMapping("/{id}/withdraw")
	public ResponseEntity<RideDTO> withdrawRide(@PathVariable long id){
		return new ResponseEntity<RideDTO>(new RideDTO(), HttpStatus.OK);
	}
	
	@PutMapping("/{id}/panic")
	public ResponseEntity<PanicDTO> panicRide(@RequestBody String reason) {
		return new ResponseEntity<PanicDTO>(new PanicDTO(), HttpStatus.OK);
	}
	
	@PutMapping("/{id}/accept")
	public ResponseEntity<RideDTO> acceptRide(@PathVariable long id) {
		return new ResponseEntity<RideDTO>(new RideDTO(), HttpStatus.OK);
	}
	
	@PutMapping("/{id}/end")
	public ResponseEntity<RideDTO> endRide(@PathVariable long id) {
		return new ResponseEntity<RideDTO>(new RideDTO(), HttpStatus.OK);
	}
	
	@PutMapping("/{id}/cancel")
	public ResponseEntity<RideDTO> reasonCancelRide(@PathVariable long id, @RequestBody String reason) {
		return new ResponseEntity<RideDTO>(new RideDTO(), HttpStatus.OK);
	}
	
}
