package com.shuttle.ride;

import java.util.List;
import java.util.Optional;

import com.shuttle.driver.Driver;
import com.shuttle.ride.dto.CreateRideDTO;

public interface IRideService {
	Ride createRide(Ride ride);
	Driver findMostSuitableDriver(CreateRideDTO createRideDTO) throws NoAvailableDriverException;
	Optional<Ride> findPendingRideForDriver(Driver driver);
}
