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
	
	
	
	public PanicDTO() {
		super();
	}
	public PanicDTO(long id, UserDTO user, RideDTO ride, ZonedDateTime time, String reason) {
		super();
		this.id = id;
		this.user = user;
		this.ride = ride;
		this.time = time;
		this.reason = reason;
	}
	public static PanicDTO getMock() {
		return new PanicDTO(10, UserDTO.getMock(), RideDTO.getMock(), ZonedDateTime.now(), "Driver is drinking while driving");
	}
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
