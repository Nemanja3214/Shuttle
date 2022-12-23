package com.shuttle.ride;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shuttle.driver.Driver;
import com.shuttle.driver.IDriverRepository;
import com.shuttle.ride.Ride.Status;
import com.shuttle.ride.dto.CreateRideDTO;

class NoAvailableDriverException extends Throwable {
	private static final long serialVersionUID = -2718176046357707329L;
}

@Service
public class RideService implements IRideService {
	@Autowired
	private IRideRepository rideRepository;
	@Autowired
	private IDriverRepository driverRepository;
	
	@Override
	public Ride createRide(Ride ride) {
		rideRepository.save(ride);	
		return ride;
	}
	
	@Override
	public Driver findMostSuitableDriver(CreateRideDTO createRideDTO) throws NoAvailableDriverException {
		final List<Driver> potentialDrivers = findPotentialDrivers();
		final Driver driver = pickBestDriver(potentialDrivers, createRideDTO);
		return driver;
	}

	/**
	 * @return List of all Drivers that are currently logged in and which can potentially perform the requested ride.
	 * @throws NoAvailableDriverException If no driver is currently active or all are busy in the future.
	 */
	private List<Driver> findPotentialDrivers() throws NoAvailableDriverException {
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
	 * @param createRideDTO The ride whose driver we're picking.
	 * @return Most suitable driver for this ride (based on variables like distance etc.).
	 */
	private Driver pickBestDriver(List<Driver> potentialDrivers, CreateRideDTO createRideDTO) {
		// Precondition: potentialDrivers.size() != 0.
		return potentialDrivers.get(0);
	}

	@Override
	public Ride findById(Long id) {
		return rideRepository.findById(id).orElse(null);
	}

	@Override
	public Ride rejectRide(Ride ride) {
		ride.setStatus(Status.Rejected);
		ride = rideRepository.save(ride);
		return ride;
	}

	@Override
	public Ride findCurrentRideByDriver(Driver driver) {
		List<Ride> pending = rideRepository.findByDriverAndStatus(driver, Status.Pending);
		List<Ride> accepted = rideRepository.findByDriverAndStatus(driver, Status.Accepted);
		List<Ride> active = rideRepository.findByDriverAndStatus(driver, Status.Active);
		
		if (pending.size() != 0) {
			return pending.get(0);
		}
		if (accepted.size() != 0) {
			return accepted.get(0);
		}
		if (active.size() != 0) {
			return active.get(0);
		}
		
		return null;
	}

}
