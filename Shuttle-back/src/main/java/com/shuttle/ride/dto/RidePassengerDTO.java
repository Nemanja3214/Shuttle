package com.shuttle.ride.dto;

import com.shuttle.passenger.Passenger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RidePassengerDTO {
	private Long id;
	private String email;

	public RidePassengerDTO(Passenger passenger) {
		this.id = passenger.getId();
		this.email = passenger.getEmail();
	}
}
