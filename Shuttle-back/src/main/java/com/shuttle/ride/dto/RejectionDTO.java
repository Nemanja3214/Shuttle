package com.shuttle.ride.dto;

import java.time.ZonedDateTime;

public class RejectionDTO {

	private String reason;
	private ZonedDateTime timeOfRejection;
	
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
	
	
}
