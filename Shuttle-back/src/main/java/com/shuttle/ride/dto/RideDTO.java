package com.shuttle.ride.dto;

import java.time.ZonedDateTime;

public class RideDTO extends BaseRideDTO{

	private String status;

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}
