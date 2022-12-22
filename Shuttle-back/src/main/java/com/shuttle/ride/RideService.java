package com.shuttle.ride;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shuttle.driver.Driver;
import com.shuttle.driver.IDriverRepository;
import com.shuttle.location.ILocationService;
import com.shuttle.location.IRouteRepository;
import com.shuttle.location.Location;
import com.shuttle.location.Route;
import com.shuttle.location.dto.LocationDTO;
import com.shuttle.location.dto.RouteDTO;
import com.shuttle.passenger.IPassengerRepository;
import com.shuttle.passenger.Passenger;
import com.shuttle.ride.Ride.Status;
import com.shuttle.ride.dto.CreateRideDTO;
import com.shuttle.vehicle.IVehicleRepository;
import com.shuttle.vehicle.IVehicleTypeRepository;
import com.shuttle.vehicle.Vehicle;
import com.shuttle.vehicle.VehicleType;

class NoAvailableDriverException extends Throwable {
	private static final long serialVersionUID = -2718176046357707329L;
}

@Service
public class RideService implements IRideService {
	@Autowired
	private IRideRepository rideRepository;
	@Autowired
	private IDriverRepository driverRepository;
	@Autowired
	private IVehicleTypeRepository vehicleTypeRepository;
	@Autowired
	private IVehicleRepository vehicleRepository;
	@Autowired
	private IPassengerRepository passengerRepository;
	@Autowired
	private ILocationService locationService;
	@Autowired
	private IRouteRepository routeRepository;
	
	// TODO: Use services instead of repositories?
	
	@Override
	public Ride createRide(CreateRideDTO rideDTO) throws NoAvailableDriverException {
		final List<Driver> potentialDrivers = findPotentialDrivers(rideDTO);
		final Driver driver = findMostSuitableDriver(potentialDrivers, rideDTO);
		
		Ride ride = getRideRequest(rideDTO, driver);
		rideRepository.save(ride);
		
		return ride;
		
		// Send notification to the Driver.
	}
	
	/**
	 * @return List of all Drivers that are currently logged in and which can potentially perform the requested ride.
	 * @throws NoAvailableDriverException If no driver is currently active or all are busy in the future.
	 */
	private List<Driver> findPotentialDrivers(CreateRideDTO rideDTO) throws NoAvailableDriverException {
		List<Driver> potentialDrivers = new ArrayList<>();
		
		List<Driver> loggedIn = driverRepository.findAllLoggedIn();
		if (loggedIn.size() == 0) {
			throw new NoAvailableDriverException();
		}
		
		List<Driver> availableDrivers = driverRepository.findAllLoggedInAvailable();
		
		if (availableDrivers.size() == 0) {
			List<Driver> driversWithoutScheduledRide = driverRepository.findAllLoggedInNotAvailable(); // TODO: Check if the driver has no future rides scheduled.
			
			if (driversWithoutScheduledRide.size() == 0) {
				throw new NoAvailableDriverException();
			} else {
				potentialDrivers = driversWithoutScheduledRide;
			}
		} else {
			potentialDrivers = availableDrivers;
		}		
		
		return potentialDrivers;
	}
	
	/**
	 * @param potentialDrivers List of all potential drivers from which the result is picked.
	 * @param rideDTO The ride.
	 * @return Most suitable driver for this ride (based on variables like distance etc.).
	 */
	private Driver findMostSuitableDriver(List<Driver> potentialDrivers, CreateRideDTO rideDTO) {
		// Precondition: potentialDrivers.size() != 0.
		return potentialDrivers.get(0);
	}


	// TODO extract special service just for this method (and other conversions).
	private Ride getRideRequest(CreateRideDTO rideDTO, Driver driver) {
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
		
		// .stream().toList() returns an *immutable* list, hence the new ArrayList<...>().
		
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

	@Override
	public Optional<Ride> findPendingRideForDriver(Driver driver) {
		List<Ride> allPending = rideRepository.findByDriverAndStatus(driver, Status.Pending);
		
		if (allPending.size() == 0) {
			return Optional.empty();
		} else {
			return Optional.of(allPending.get(0));
		}
	}
}
