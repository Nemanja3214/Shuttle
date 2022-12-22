package com.shuttle.ride.dto;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.shuttle.location.Location;
import com.shuttle.location.dto.LocationDTO;
import com.shuttle.location.dto.RouteDTO;
import com.shuttle.ride.Ride;
import com.shuttle.ride.cancellation.Cancellation;
import com.shuttle.ride.cancellation.dto.CancellationDTO;
import com.shuttle.vehicle.VehicleType;

import lombok.AllArgsConstructor;
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

	/**
	 * Create a RideDTO.
	 * @param ride Base Ride objects.
	 * @param cancellation Cancellation (if any), can be null.
	 */
	public RideDTO(Ride ride, Cancellation cancellation) {
		this.id = ride.getId();
		this.locations = new ArrayList<RouteDTO>();

		List<Location> ls = ride.getLocations();
		
		for (int i = 0; i < ls.size(); i += 2) {
			LocationDTO from = LocationDTO.from(ls.get(i));
			LocationDTO to = LocationDTO.from(ls.get(i + 1));
			
			RouteDTO d = new RouteDTO(from, to);
			locations.add(d);
		}

		if (ride.getStartTime() != null) {
			this.startTime = ride.getStartTime().format(DateTimeFormatter.ISO_DATE_TIME);
		}
		
		if (ride.getEndTime() != null) {
			this.endTime = ride.getEndTime().format(DateTimeFormatter.ISO_DATE_TIME);
		}
		
		this.totalCost = ride.getTotalCost();
		this.driver = new RideDriverDTO(ride.getDriver());
		this.passengers = ride.getPassengers().stream().map(p -> new RidePassengerDTO(p)).toList();
		this.estimatedTimeInMinutes = ride.getEstimatedTimeInMinutes();
		this.babyTransport = ride.getBabyTransport();
		this.petTransport = ride.getPetTransport();
		this.vehicleType = null; // TODO: Fetch from database.

		if (cancellation != null) {
			this.rejection = new CancellationDTO(cancellation);
		}
		
		this.status = ride.getStatus();
	}
}
