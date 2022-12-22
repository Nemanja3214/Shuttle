package com.shuttle.ride.dto;

import java.util.List;

import com.shuttle.passenger.Passenger;
import com.shuttle.passenger.PassengerDTO;
import com.shuttle.ride.Ride;

public class RidePageDTO {
	public long totalCount;
	public List<RideDTO> results;
	
	public RidePageDTO(List<Ride> rides) {
		// TODO: Mapping needs RideController.
		results = rides.stream().map(r -> new RideDTO()).toList();
		totalCount = results.size();
	}
}
