package com.shuttle.ride.dto;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.shuttle.location.dto.LocationPairDTO;
import com.shuttle.user.dto.BasicUserInfoDTO;

public class RideDTO{

	private long id;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private double totalCost;
	private BasicUserInfoDTO driver;
	private List<BasicUserInfoDTO> passengers;
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
	
	public RideDTO(long id, LocalDateTime startTime, LocalDateTime endTime, double totalCost, BasicUserInfoDTO driver,
			List<BasicUserInfoDTO> passengers, int estimatedTimeInMinutes, String vehicleType, boolean babyTransport,
			boolean petTransport, RejectionDTO rejection, List<LocationPairDTO> locations, String status) {
		super();
		this.id = id;
		this.startTime = startTime;
		this.endTime = endTime;
		this.totalCost = totalCost;
		this.driver = driver;
		this.passengers = passengers;
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
		
		List<BasicUserInfoDTO> passengers = new ArrayList<>();
		passengers.add(BasicUserInfoDTO.getMock());
		return new RideDTO(123, LocalDateTime.now(), LocalDateTime.now(), 1260, BasicUserInfoDTO.getMock(), passengers, 5, "STANDARDNO",
				true, true, RejectionDTO.getMock(), locations, "PENDING");
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public LocalDateTime getStartTime() {
		return startTime;
	}
	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}
	public LocalDateTime getEndTime() {
		return endTime;
	}
	public void setEndTime(LocalDateTime endTime) {
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

	public BasicUserInfoDTO getDriver() {
		return driver;
	}

	public void setDriver(BasicUserInfoDTO driver) {
		this.driver = driver;
	}

	public List<BasicUserInfoDTO> getPassengers() {
		return passengers;
	}

	public void setPassengers(List<BasicUserInfoDTO> passengers) {
		this.passengers = passengers;
	}

	
}
