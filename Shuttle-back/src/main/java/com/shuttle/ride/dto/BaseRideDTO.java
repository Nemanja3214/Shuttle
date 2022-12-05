package com.shuttle.ride.dto;

import java.time.ZonedDateTime;
import java.util.Collection;

import com.shuttle.location.dto.LocationPairDTO;

// used in notification and user getRides
// FIX ALL
public class BaseRideDTO {
	protected ZonedDateTime startTime;
	protected ZonedDateTime endTime;
	protected double totalCost;
//	DriverDTO
//	List<PassengerDTO>
	protected int estimatedTimeInMinutes;
	protected String vehicleType;
	protected boolean babyTransport;
	protected boolean petTransport;
	Collection<LocationPairDTO> locations;
	

	public Collection<LocationPairDTO> getLocations() {
		return locations;
	}
	public void setLocations(Collection<LocationPairDTO> locations) {
		this.locations = locations;
	}
	public ZonedDateTime getStartTime() {
		return startTime;
	}
	public void setStartTime(ZonedDateTime startTime) {
		this.startTime = startTime;
	}
	public ZonedDateTime getEndTime() {
		return endTime;
	}
	public void setEndTime(ZonedDateTime endTime) {
		this.endTime = endTime;
	}
	public double getTotalCost() {
		return totalCost;
	}
	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
	}
	public int getEstimatedTimeInMinutes() {
		return estimatedTimeInMinutes;
	}
	public void setEstimatedTimeInMinutes(int estimatedTimeInMinutes) {
		this.estimatedTimeInMinutes = estimatedTimeInMinutes;
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
