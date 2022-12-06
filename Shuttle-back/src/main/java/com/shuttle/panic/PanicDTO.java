package com.shuttle.panic;

import java.time.ZonedDateTime;

import com.shuttle.ride.dto.RideDTO;
import com.shuttle.user.dto.UserDTO;

public class PanicDTO {
	
	private long id;
	private UserDTO user;
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
	public UserDTO getUser() {
		return user;
	}
	public void setUser(UserDTO user) {
		this.user = user;
	}
	
	

}
