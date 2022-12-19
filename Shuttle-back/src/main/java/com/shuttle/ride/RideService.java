package com.shuttle.ride;

import java.io.NotActiveException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shuttle.driver.Driver;
import com.shuttle.driver.IDriverRepository;
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
	public void createRide(CreateRideDTO rideDTO) throws NoAvailableDriverException {
		final List<Driver> potentialDrivers = findPotentialDrivers();
		final Driver driver = findMostSuitableDriver(potentialDrivers, rideDTO);
		
		// Create Ride from rideDTO.
		// Send notification to the Driver.
	}
	
	private List<Driver> findPotentialDrivers() throws NoAvailableDriverException {
		List<Driver> potentialDrivers = new ArrayList<>();
		
		List<Driver> activeDrivers = new ArrayList<>();
		if (activeDrivers.size() == 0) {
			throw new NoAvailableDriverException();
		}
		
		List<Driver> availableDrivers = new ArrayList<>();
		
		if (availableDrivers.size() == 0) {
			List<Driver> driversWithoutScheduledRide = new ArrayList<>();
			
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
	
	
	private Driver findMostSuitableDriver(List<Driver> potentialDrivers, CreateRideDTO rideDTO) {
		// Pre-condition: potentialDrivers.size() != 0.
		return potentialDrivers.get(0);
	}

}
