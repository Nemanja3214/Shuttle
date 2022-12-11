package com.shuttle.ride.dto;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;

import com.shuttle.driver.DriverDTO;
import com.shuttle.location.dto.LocationPairDTO;
import com.shuttle.user.dto.UserDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RideDTO{

	private long id;
	private ZonedDateTime startTime;
	private ZonedDateTime endTime;
	private double totalCost;
	private DriverDTO driver;
	private List<UserDTO> passengers;
	private int estimatedTimeInMinutes;
	private String vehicleType;
	private boolean babyTransport;
	private boolean petTransport;
	private RejectionDTO rejection;
	private String status;
}