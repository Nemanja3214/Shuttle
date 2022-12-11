package com.shuttle.ride.dto;

import java.util.ArrayList;
import java.util.List;

import com.shuttle.location.dto.LocationPairDTO;
import com.shuttle.user.dto.BasicUserInfoDTO;

public class CreateRideDTO {
	
	private List<BasicUserInfoDTO> passengers;
	private List<LocationPairDTO> locations;
	private String vehicleType;
	private boolean babyTransport;
	private boolean petTransport;
	
	
		
	
	public CreateRideDTO() {
		super();
	}
	public List<LocationPairDTO> getLocations() {
		return locations;
	}
	public void setLocations(List<LocationPairDTO> locations) {
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
	public List<BasicUserInfoDTO> getPassengers() {
		return passengers;
	}
	public void setPassengers(List<BasicUserInfoDTO> passengers) {
		this.passengers = passengers;
	}
	
	

}
