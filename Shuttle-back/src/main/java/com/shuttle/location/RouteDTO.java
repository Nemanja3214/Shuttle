package com.shuttle.location;

public class RouteDTO {
	public String address;
	public Double latitude;
	public Double longitude;
	public LocationDTO departure;
	public LocationDTO destination;
	
	public RouteDTO(LocationDTO departure, LocationDTO destination) {
		this.address = departure.address;
		this.latitude = departure.latitude;
		this.longitude = departure.longitude;
		this.departure = departure;
		this.destination = destination;
	}
}
