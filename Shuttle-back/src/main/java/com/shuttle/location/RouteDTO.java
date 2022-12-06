package com.shuttle.location;

public class RouteDTO {
	public LocationDTO departure;
	public LocationDTO destination;
	
	public RouteDTO(LocationDTO departure, LocationDTO destination) {
		this.departure = departure;
		this.destination = destination;
	}
}
