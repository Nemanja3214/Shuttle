package com.shuttle.ride.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.shuttle.location.dto.LocationDTO;
import com.shuttle.location.dto.RouteDTO;

public class CreateRideEstimationDTO {
	private List<RouteDTO> locations;
	private String vehicleType;
	private boolean babyTransport;
	private boolean petTransport;
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


	public long getTravelTime() {
		return travelTime;
	}

	public void setTravelTime(long travelTime) {
		this.travelTime = travelTime;
	}

	public double calculateLength() {
		return locations.stream().map(route -> route.getDistance()).collect(Collectors.summingDouble(Double::doubleValue));
	}
	
	public static void main(String[] args) {
		List<RouteDTO> routes = new ArrayList<>();

		
//		routes.add(makeRouteDTO(45, 20, 46, 21));
//		routes.add(makeRouteDTO(45, 20, 46, 21));
		
		routes.add(makeRouteDTO(10, 20, 30, 40));
		
		System.out.println(routes.stream().map(route -> route.getDistance()).collect(Collectors.summingDouble(Double::doubleValue)));

	}
	
	public static RouteDTO makeRouteDTO(double lat1, double lon1, double lat2, double lon2) {
		
		LocationDTO departure = new LocationDTO( );
		departure.setLatitude(lat1);
		departure.setLongitude(lon1);
		
		LocationDTO destination = new LocationDTO();
		destination.setLatitude(lat2);
		destination.setLongitude(lon2);
		
		RouteDTO routeDTO = new RouteDTO();
		routeDTO.setDeparture(departure);
		routeDTO.setDestination(destination);
		
		return routeDTO;
	}
	

}
