package com.shuttle.ride;

import java.util.List;
import java.util.Optional;

import com.shuttle.driver.Driver;
import com.shuttle.ride.dto.CreateRideDTO;

public interface IRideService {
	Ride createRide(Ride ride);
	Driver findMostSuitableDriver(CreateRideDTO createRideDTO) throws NoAvailableDriverException;
	
	/**
	 * Find ride which is either PENDING, ACCEPTED or ACTIVE for this driver.
	 * Searching is done in the order above with short circuiting.
	 * @param driver The driver.
	 * @return The ride or null if none found.
	 */
	Ride findCurrentRideByDriver(Driver driver);
	
	/**
	 * Returns the ride from the given ID.
	 * @param id ID to search for. Must not be null.
	 * @return Ride object with the matching ID or null if none found.
	 */
	Ride findById(Long id);
	
	/**
	 * Reject the ride (by driver).
	 * @param ride The ride that's rejected.
	 */
	Ride rejectRide(Ride ride);
}
