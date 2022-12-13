package com.shuttle.ride;

import java.time.LocalDateTime;

import com.shuttle.common.Entity;
import com.shuttle.user.User;
import jakarta.persistence.ManyToOne;

public class Rejection extends Entity {
	private Ride ride;
	private String reason;
	@ManyToOne
	private User user;
	private LocalDateTime timeOfRejection;

	public Ride getRide() {
		return ride;
	}

	public void setRide(Ride ride) {
		this.ride = ride;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public LocalDateTime getTimeOfRejection() {
		return timeOfRejection;
	}

	public void setTimeOfRejection(LocalDateTime timeOfRejection) {
		this.timeOfRejection = timeOfRejection;
	}
}
