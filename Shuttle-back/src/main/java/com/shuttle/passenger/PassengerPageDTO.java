package com.shuttle.passenger;

import java.util.List;

public class PassengerPageDTO {
	public int totalCount;
	public List<PassengerDTO> results;
	
	public PassengerPageDTO(List<Passenger> passengers) {
		results = passengers.stream().map(p -> new PassengerDTO(p)).toList();
		totalCount = results.size();
	}
}
