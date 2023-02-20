package com.shuttle.location.dto;

import com.shuttle.location.Location;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationDTO {
	private String address;
	private Double latitude;
	private Double longitude;
	
	public static LocationDTO from(Location l) {
		return new LocationDTO(l.getAddress(), l.getLatitude(), l.getLongitude());
	}
	
	public Location to() {
		Location l = new Location();
		l.setAddress(address);
		l.setLatitude(latitude);
		l.setLongitude(longitude);
		return l;
	}
	
	public static LocationDTO getMock() {
		return new LocationDTO("Bulevar oslobodjenja 46", 45.267136, 19.833549);
	}

	public LocationDTO(Location location) {
		this.setAddress(location.getAddress());
		this.setLatitude(location.getLatitude());
		this.setLongitude(location.getLongitude());
	}
}