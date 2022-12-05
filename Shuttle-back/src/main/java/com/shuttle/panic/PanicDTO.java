package com.shuttle.panic;

import java.time.ZonedDateTime;

import com.shuttle.ride.dto.RideDTO;

public class PanicDTO {
	
	private long id;
//	UserDTO
	private RideDTO ride;
	private ZonedDateTime time;
	private String reason;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public RideDTO getRide() {
		return ride;
	}
	public void setRide(RideDTO ride) {
		this.ride = ride;
	}
	public ZonedDateTime getTime() {
		return time;
	}
	public void setTime(ZonedDateTime time) {
		this.time = time;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	
	

}
