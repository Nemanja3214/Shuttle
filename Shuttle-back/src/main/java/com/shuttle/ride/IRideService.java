package com.shuttle.ride;

import java.util.List;

import com.shuttle.driver.Driver;
import com.shuttle.ride.dto.CreateRideDTO;

public interface IRideService {
	Ride createRide(CreateRideDTO rideDTO) throws NoAvailableDriverException;
	List<Ride> findPendingRidesForDriver(Driver driver);
}
