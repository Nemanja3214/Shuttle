package com.shuttle.review.dto;

import com.shuttle.passenger.Passenger;

public class ReviewPassengerDTO {
	public Long id;
	public String email;
	
	public ReviewPassengerDTO(Passenger p) {
		this.id = p.getId();
		this.email = p.getCredentials().getEmail();
	}
}
