package com.shuttle.ride.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.shuttle.location.Location;
import com.shuttle.location.LocationDTO;
import com.shuttle.location.RouteDTO;
import com.shuttle.ride.Rejection;
import com.shuttle.ride.Ride;
import com.shuttle.vehicle.Vehicle;

public class RideDTO {
	public Long id;
	public List<RouteDTO> locations;
	public LocalDateTime startTime;
	public LocalDateTime endTime;
	public Integer totalCost;
	public RideDriverDTO driver;
	public List<RidePassengerDTO> passengers;
	public Integer estimatedTimeInMinutes;
	public Boolean babyTransport;
	public Boolean petTransport;
	public Vehicle.Type vehicleType;
	public RejectionDTO rejection;
	
	public RideDTO(Ride ride) {
		this.id = ride.getId();
		this.locations = new ArrayList<RouteDTO>();
		this.locations.add(new RouteDTO(new LocationDTO(new Location()), new LocationDTO(new Location())));// ride.getLocations().stream().map(l -> new LocationDTO(l)).toList();
		this.startTime = ride.getStartTime();
		this.endTime = ride.getEndTime();
		this.totalCost = ride.getTotalCost();
		this.driver = new RideDriverDTO(ride.getDriver());
		this.passengers = ride.getPassengers().stream().map(p -> new RidePassengerDTO(p)).toList();
		this.estimatedTimeInMinutes = ride.getEstimatedTimeInMinutes();
		this.babyTransport = ride.getBabyTransport();
		this.petTransport = ride.getPetTransport();
		this.vehicleType = ride.getVehicleType();
		this.rejection = new RejectionDTO(new Rejection());
	}
}
