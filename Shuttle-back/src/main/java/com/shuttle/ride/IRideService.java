package com.shuttle.ride;

import java.util.List;
import java.util.Optional;

import com.shuttle.driver.Driver;
import com.shuttle.passenger.Passenger;
import com.shuttle.ride.cancellation.Cancellation;
import com.shuttle.ride.dto.CreateRideDTO;

public interface IRideService {
	Ride createRide(Ride ride);
	
	/**
	 * Find a driver best suited for performing this ride.
	 * The decision is made using a proximity heuristic.
	 * @param createRideDTO The ride that's being created.
	 * @return Instance of the driver most suited for this ride.
	 * @throws NoAvailableDriverException If no driver can be found.
	 */
	Driver findMostSuitableDriver(CreateRideDTO createRideDTO) throws NoAvailableDriverException;
	
	/**
	 * Find ride which is either ACCEPTED or PENDING for this driver.
	 * Searching is done in the order above with short circuiting.
	 * @param driver The driver.
	 * @return The ride or null if none found.
	 */
	Ride findCurrentRideByDriver(Driver driver);
	
	/**
	 * Find ride of this driver that's ACTIVE.
	 * This is a subset of findCurrentRideByDriver
	 * @param driver The driver.
	 * @return The ride or null if none found.
	 */
	Ride findCurrentRideByDriverInProgress(Driver driver);
	
	/**
	 * Returns the ride from the given ID.
	 * @param id ID to search for. Must not be null.
	 * @return Ride object with the matching ID or null if none found.
	 */
	Ride findById(Long id);
	
	/**
	 * Reject the ride (by driver).
	 * @param ride The ride that's rejected.
     * @param cancellation Reason for rejection.
	 * @return The ride.
	 */
	Ride rejectRide(Ride ride, Cancellation cancellation);

	/**
	 * Accept and start the ride (by driver).
	 * @param ride The ride that's accepted.
	 * @return The ride.
	 */
	Ride acceptRide(Ride ride);
	
	
	/**
	 * Finish the ride (by driver).
	 * @param ride The ride that's finished.
	 * @return The ride.
	 */
	Ride finishRide(Ride ride);

    /**
     * Find PENDING or ACCEPTED Ride from the provided passenger.
     * If both exist, ACCEPTED is given priority.
     * @param passenger The passenger. Must not be null.
     * @return The ride or null.
     */
    Ride findActiveOrPendingByPassenger(Passenger passenger);
}
