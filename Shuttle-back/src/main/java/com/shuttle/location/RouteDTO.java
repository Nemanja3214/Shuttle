package com.shuttle.location;

public class RouteDTO {
	public LocationDTO departure;
	public LocationDTO destination;
	
	public RouteDTO(Route r) {
		this.departure = new LocationDTO(r.getDeparture());
		this.destination = new LocationDTO(r.getDestination());

	}
	
	public RouteDTO(LocationDTO departure, LocationDTO destination) {
		this.departure = departure;
		this.destination = destination;
	}
}
