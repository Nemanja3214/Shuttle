package com.shuttle.ride.dto;

import com.shuttle.passenger.Passenger;

public class RidePassengerDTO {
	public Long id;
	public String email;
	public final String type = "PASSENGER";
	
	public RidePassengerDTO(Passenger passenger) {
		this.id = passenger.getId();
		this.email = passenger.getEmail();
	}
}
