package com.shuttle.ride.dto;

import java.util.List;

import com.shuttle.driver.dto.DriverDTO;
import com.shuttle.location.dto.RouteDTO;
import com.shuttle.passenger.PassengerDTO;
import com.shuttle.ride.Ride;
import com.shuttle.ride.cancellation.dto.CancellationDTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RideDTO {
	private Long id;
	private List<RouteDTO> locations;
	private String startTime;
	private String endTime;
	private Double totalCost;
	private RideDriverDTO driver;
	private List<RidePassengerDTO> passengers;
	private Integer estimatedTimeInMinutes;
	private Boolean babyTransport;
	private Boolean petTransport;
	private String vehicleType;
	private CancellationDTO rejection;
	private Ride.Status status;
	private String scheduledTime;
	private Double totalLength;
}
