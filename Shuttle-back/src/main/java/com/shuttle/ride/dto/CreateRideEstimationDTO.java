package com.shuttle.ride.dto;

import java.util.List;

import com.shuttle.location.dto.RouteDTO;

public class CreateRideEstimationDTO {
	private List<RouteDTO> locations;
	private String vehicleType;
	private boolean babyTransport;
	private boolean petTransport;
	private double routeLength;
	private long travelTime;

	public List<RouteDTO> getLocations() {
		return locations;
	}

	public void setLocations(List<RouteDTO> locations) {
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

	public double getRouteLength() {
		return routeLength;
	}

	public void setRouteLength(double routeLength) {
		this.routeLength = routeLength;
	}

	public long getTravelTime() {
		return travelTime;
	}

	public void setTravelTime(long travelTime) {
		this.travelTime = travelTime;
	}
	
	

}
