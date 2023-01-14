package com.shuttle.ride;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;

import com.shuttle.common.exception.FavoriteRideLimitExceeded;
import com.shuttle.common.exception.NonExistantFavoriteRoute;
import com.shuttle.common.exception.NonExistantUserException;
import com.shuttle.common.exception.NonExistantVehicleType;
import com.shuttle.driver.Driver;
import com.shuttle.location.FavoriteRoute;
import com.shuttle.location.dto.CreateFavouriteRouteDTO;
import com.shuttle.passenger.Passenger;
import com.shuttle.ride.cancellation.Cancellation;
import com.shuttle.ride.dto.CreateRideDTO;
import com.shuttle.user.GenericUser;
import com.shuttle.vehicle.vehicleType.VehicleType;

public interface IRideService {
	Ride save(Ride ride);
	
	/**
	 * Find a driver best suited for performing this ride.
	 * The decision is made using a proximity heuristic.
	 * @param createRideDTO The ride that's being created.
     * @param forFuture Is the ride scheduled in the future? Setting this to true will result in the
     * ride being persisted (not in this function!) regardless of this function's return object. A
     * ride scheduled in the future can have its driver be assigned later, once a driver has no 
     * pending rides. Setting this to false means that a driver must be found now, else a ride can't
     * be created, resulting in a NoAvailableDriverException.
	 * @return Most suited driver. Can be null if forFuture is true.
	 * @throws NoAvailableDriverException If no driver can be found (and forFuture is false).
	 */
	Driver findMostSuitableDriver(CreateRideDTO createRideDTO, boolean forFuture) throws NoAvailableDriverException;
	
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
     * Cancel the ride.
     * @param ride The ride that's canceled.
     * @return The ride.
     */
    Ride cancelRide(Ride ride);

    /**
     * Find PENDING or ACCEPTED Ride from the provided passenger.
     * If both exist, ACCEPTED is given priority.
     * @param passenger The passenger. Must not be null.
     * @return The ride or null.
     */
    Ride findActiveOrPendingByPassenger(Passenger passenger);

    /**
     * Find all rides whose driver is null (scheduled in the future).
     * @return List of rides with null drivers. Can be empty.
     */
    List<Ride> findRidesWithNoDriver();

    /**
     * Extracted predicate method for determining if the driver's vehicle is suitable for the
     * given parameters.
     * @param d The driver. Must not be null.
     * @param baby True if the driver must be able to transport babies.
     * @param pet  True if the driver must be able to transport pets.
     * @param seatsNeeded Minumum number of passenger seats the driver's vehicle must have.
     * @param vehicleType Requested vehicle type.
     * @return True if satisfies all criteria, false otherwise.
     */    
    public boolean requestParamsMatch(Driver d, boolean baby, boolean pet, int seatsNeeded, VehicleType vehicleType);

    /**
     * Get all pending rides scheduled in the future.
     * @return List of such rides. Can be empty.
     */
    List<Ride> findAllPendingInFuture();

    /**
     * Find list of rides for a given user.
     * @param user User.
     * @param pageable Pageable object got from the request param.
     * @param from Datetime range from.
     * @param to Datetime range to.
     * @return List of rides.
     */
	List<Ride> findByUser(GenericUser user, Pageable pageable, LocalDateTime from, LocalDateTime to);
	List<Ride> findRidesByPassengerInDateRange(Long passengerId, String from, String to, Pageable pageable) throws NonExistantUserException;

	FavoriteRoute createFavoriteRoute(CreateFavouriteRouteDTO dto, long favLimit)
			throws NonExistantVehicleType, NonExistantUserException, FavoriteRideLimitExceeded;

	List<FavoriteRoute> getFavouriteRoutes();

	void delete(long id) throws NonExistantFavoriteRoute;

}
