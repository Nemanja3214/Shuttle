package com.shuttle.ride.dto;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

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
	private List<LocationPairDTO> locations;
	private String status;
	
	
	
	
	public RideDTO() {
		super();
	}
	public RideDTO(long id, ZonedDateTime startTime, ZonedDateTime endTime, double totalCost,
			int estimatedTimeInMinutes, String vehicleType, boolean babyTransport, boolean petTransport,
			RejectionDTO rejection, List<LocationPairDTO> locations, String status) {
		super();
		this.id = id;
		this.startTime = startTime;
		this.endTime = endTime;
		this.totalCost = totalCost;
		this.estimatedTimeInMinutes = estimatedTimeInMinutes;
		this.vehicleType = vehicleType;
		this.babyTransport = babyTransport;
		this.petTransport = petTransport;
		this.rejection = rejection;
		this.locations = locations;
		this.status = status;
	}
	public static RideDTO getMock() {
		List<LocationPairDTO> locations = new ArrayList<>();
		locations.add(LocationPairDTO.getMock());
		return new RideDTO(123, ZonedDateTime.now(), ZonedDateTime.now(), 1260, 5, "STANDARDNO",
				true, true, RejectionDTO.getMock(), locations, "PENDING");
	}
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
	
	public List<LocationPairDTO> getLocations() {
		return locations;
	}
	public void setLocations(List<LocationPairDTO> locations) {
		this.locations = locations;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	
}
