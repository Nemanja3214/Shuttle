package com.shuttle.ride.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.shuttle.location.Location;
import com.shuttle.location.LocationDTO;
import com.shuttle.location.Route;
import com.shuttle.location.RouteDTO;
import com.shuttle.ride.Rejection;
import com.shuttle.ride.Ride;
import com.shuttle.vehicle.Vehicle;

public class RideDTO {
	private Long id;
	private List<RouteDTO> locations;
	private String startTime;
	private String endTime;
	private Integer totalCost;
	private RideDriverDTO driver;
	private List<RidePassengerDTO> passengers;
	private Integer estimatedTimeInMinutes;
	private Boolean babyTransport;
	private Boolean petTransport;
	private Vehicle.Type vehicleType;
	private RejectionDTO rejection;
	private String status;

	public RideDTO(Ride ride) {
		this.id = ride.getId();
		this.locations = new ArrayList<RouteDTO>();

		List<Location> ls = ride.getLocations().stream().toList();
		for (int i = 0; i < ls.size(); i += 2) {
			RouteDTO d = new RouteDTO(new LocationDTO(ls.get(i)), new LocationDTO(ls.get(i + 1)));
			locations.add(d);
		}

		// this.locations.add(new RouteDTO(new LocationDTO(new Location()), new
		// LocationDTO(new Location())));// ride.getLocations().stream().map(l -> new
		// LocationDTO(l)).toList();
		this.startTime = ride.getStartTime().format(DateTimeFormatter.ISO_DATE_TIME);
		this.endTime = ride.getEndTime().format(DateTimeFormatter.ISO_DATE_TIME);
		this.totalCost = ride.getTotalCost();
		this.driver = new RideDriverDTO(ride.getDriver());
		this.passengers = ride.getPassengers().stream().map(p -> new RidePassengerDTO(p)).toList();
		this.estimatedTimeInMinutes = ride.getEstimatedTimeInMinutes();
		this.babyTransport = ride.getBabyTransport();
		this.petTransport = ride.getPetTransport();
		this.vehicleType = ride.getVehicleType();
		this.rejection = new RejectionDTO(new Rejection());
	}

	public RideDTO() {
		super();
	}

	public RideDTO(long id, LocalDateTime startTime, LocalDateTime endTime, Integer totalCost, RideDriverDTO driver,
			List<RidePassengerDTO> passengers, int estimatedTimeInMinutes, Vehicle.Type vehicleType,
			boolean babyTransport, boolean petTransport, RejectionDTO rejection, List<RouteDTO> locations,
			String status) {
		super();
		this.id = id;
		this.startTime = startTime.format(DateTimeFormatter.ISO_DATE_TIME);
		this.endTime = endTime.format(DateTimeFormatter.ISO_DATE_TIME);
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

//	public static RideDTO getMock() {
//		List<LocationPairDTO> locations = new ArrayList<>();
//		locations.add(LocationPairDTO.getMock());
//		
//		List<BasicUserInfoDTO> passengers = new ArrayList<>();
//		passengers.add(BasicUserInfoDTO.getMock());
//		return new RideDTO(123, LocalDateTime.now(), LocalDateTime.now(), 1260, BasicUserInfoDTO.getMock(), passengers, 5, "STANDARDNO",
//				true, true, RejectionDTO.getMock(), locations, "PENDING");
//	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<RouteDTO> getLocations() {
		return locations;
	}

	public void setLocations(List<RouteDTO> locations) {
		this.locations = locations;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public Integer getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(Integer totalCost) {
		this.totalCost = totalCost;
	}

	public RideDriverDTO getDriver() {
		return driver;
	}

	public void setDriver(RideDriverDTO driver) {
		this.driver = driver;
	}

	public List<RidePassengerDTO> getPassengers() {
		return passengers;
	}

	public void setPassengers(List<RidePassengerDTO> passengers) {
		this.passengers = passengers;
	}

	public Integer getEstimatedTimeInMinutes() {
		return estimatedTimeInMinutes;
	}

	public void setEstimatedTimeInMinutes(Integer estimatedTimeInMinutes) {
		this.estimatedTimeInMinutes = estimatedTimeInMinutes;
	}

	public Boolean getBabyTransport() {
		return babyTransport;
	}

	public void setBabyTransport(Boolean babyTransport) {
		this.babyTransport = babyTransport;
	}

	public Boolean getPetTransport() {
		return petTransport;
	}

	public void setPetTransport(Boolean petTransport) {
		this.petTransport = petTransport;
	}

	public Vehicle.Type getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(Vehicle.Type vehicleType) {
		this.vehicleType = vehicleType;
	}

	public RejectionDTO getRejection() {
		return rejection;
	}

	public void setRejection(RejectionDTO rejection) {
		this.rejection = rejection;
	}

}
