package com.shuttle.ride.dto;

import java.util.Collection;

import com.shuttle.location.dto.LocationDTO;

public class CreateRideDTO {
	
//	List<PassengerCreationDTO> missing
	
	private Collection<LocationDTO> locations;
	private String vehicleType;
	private boolean babyTransport;
	private boolean petTransport;
	
	
	public Collection<LocationDTO> getLocations() {
		return locations;
	}
	public void setLocations(Collection<LocationDTO> locations) {
		this.locations = locations;
	}
	public String getVehicleType() {
		return vehicleType;
	}
	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}
	public boolean isBabyTransport() {
		return babyTransport;
	}
	public void setBabyTransport(boolean babyTransport) {
		this.babyTransport = babyTransport;
	}
	public boolean isPetTransport() {
		return petTransport;
	}
	public void setPetTransport(boolean petTransport) {
		this.petTransport = petTransport;
	}
	
	

}
