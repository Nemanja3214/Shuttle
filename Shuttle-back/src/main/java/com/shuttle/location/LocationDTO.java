package com.shuttle.location;

public class LocationDTO {
	public String address;
	public Double latitude;
	public Double longitude;
	
	public LocationDTO(Location location) {
		this.address = location.getAddress();
		this.latitude = location.getLatitude();
		this.longitude = location.getLongitude();
	}
}
