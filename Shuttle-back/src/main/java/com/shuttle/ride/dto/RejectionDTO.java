package com.shuttle.ride.dto;

import java.time.ZonedDateTime;

public class RejectionDTO {

	private String reason;
	private ZonedDateTime timeOfRejection;
	
	
	
	public RejectionDTO() {
		super();
	}
	public RejectionDTO(String reason, ZonedDateTime timeOfRejection) {
		super();
		this.reason = reason;
		this.timeOfRejection = timeOfRejection;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public ZonedDateTime getTimeOfRejection() {
		return timeOfRejection;
	}
	public void setTimeOfRejection(ZonedDateTime timeOfRejection) {
		this.timeOfRejection = timeOfRejection;
	}
	public static RejectionDTO getMock() {
		return new RejectionDTO("Ride is canceled due to previous problems with the passenger", ZonedDateTime.now());
	}
	
	
}
