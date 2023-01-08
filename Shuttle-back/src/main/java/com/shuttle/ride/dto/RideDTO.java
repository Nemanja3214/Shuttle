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
	public RideDTO(Ride ride) {
		this.id = ride.getId();
		this.locations = RouteDTO.getRoutes(ride.getRoute());
		this.startTime = ride.getStartTime().toString();
		this.endTime = ride.getEndTime().toString();
		this.totalCost = ride.getTotalCost();
		this.driver = new RideDriverDTO(ride.getDriver());
		this.passengers= ride.getPassengers().stream().map(passenger -> new RidePassengerDTO(passenger)).toList() ;
		this.estimatedTimeInMinutes= ride.getEstimatedTimeInMinutes();
		this.babyTransport = ride.getBabyTransport();
		this.petTransport= ride.getPetTransport() ;
		this.vehicleType = ride.getVehicleType().toString();
		this.rejection = ride.getRejection() == null ? null :  new CancellationDTO(ride.getRejection());
		this.status= ride.getStatus() ;
		
	}
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
}
