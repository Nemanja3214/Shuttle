package com.shuttle.ride.dto;

import java.time.ZonedDateTime;
import java.util.Collection;

import com.shuttle.location.dto.LocationPairDTO;

public class RideDTO{

	private long id;
	private ZonedDateTime startTime;
	private ZonedDateTime endTime;
	private double totalCost;
//	DriverDTO
//	List<PassengerDTO>
	private int estimatedTimeInMinutes;
	private String vehicleType;
	private boolean babyTransport;
	private boolean petTransport;
	private RejectionDTO rejection;
	private Collection<LocationPairDTO> locations;
	private String status;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
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
	public RejectionDTO getRejection() {
		return rejection;
	}
	public void setRejection(RejectionDTO rejection) {
		this.rejection = rejection;
	}
	public Collection<LocationPairDTO> getLocations() {
		return locations;
	}
	public void setLocations(Collection<LocationPairDTO> locations) {
		this.locations = locations;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	
}
