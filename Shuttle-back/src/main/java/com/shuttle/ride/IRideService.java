package com.shuttle.ride;

import com.shuttle.ride.dto.CreateRideDTO;

public interface IRideService {
	void createRide(CreateRideDTO rideDTO) throws NoAvailableDriverException;
}
