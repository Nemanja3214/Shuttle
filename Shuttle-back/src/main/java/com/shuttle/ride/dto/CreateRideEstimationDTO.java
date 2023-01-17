package com.shuttle.ride.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.shuttle.location.dto.RouteDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class CreateRideEstimationDTO {
	private List<RouteDTO> locations;
	private String vehicleType;
	private Boolean babyTransport;
	private Boolean petTransport;
	private long travelTime;

	public double calculateLength() {
		return locations.stream().map(route -> route.getDistance()).collect(Collectors.summingDouble(Double::doubleValue));
	}
}
