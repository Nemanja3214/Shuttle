package com.shuttle.location.dto;

public class LocationPairDTO {
	private LocationDTO destination;
	private LocationDTO departure;
	
	
	
	public LocationPairDTO() {
		super();
	}
	public LocationPairDTO(LocationDTO destination, LocationDTO departure) {
		super();
		this.destination = destination;
		this.departure = departure;
	}
	public static LocationPairDTO getMock() {
		return new LocationPairDTO(LocationDTO.getMock(), LocationDTO.getMock());
	}
	public LocationDTO getDestination() {
		return destination;
	}
	public void setDestination(LocationDTO destination) {
		this.destination = destination;
	}
	public LocationDTO getDeparture() {
		return departure;
	}
	public void setDeparture(LocationDTO departure) {
		this.departure = departure;
	}
	
	
	

}
