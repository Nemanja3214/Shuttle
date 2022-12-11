package com.shuttle.panic;

import java.time.LocalDateTime;

import com.shuttle.common.Entity;
import com.shuttle.ride.Ride;
import com.shuttle.user.User;

public class Panic extends Entity {
	private User user;
	private Ride ride;
	private LocalDateTime time;
	private String reason;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Ride getRide() {
		return ride;
	}

	public void setRide(Ride ride) {
		this.ride = ride;
	}

	public LocalDateTime getTime() {
		return time;
	}

	public void setTime(LocalDateTime time) {
		this.time = time;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
}
