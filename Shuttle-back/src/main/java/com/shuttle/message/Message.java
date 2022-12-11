package com.shuttle.message;

import java.time.LocalDateTime;

import com.shuttle.common.Entity;
import com.shuttle.ride.Ride;
import com.shuttle.user.User;

public class Message extends Entity {
	private User sender;
	private User receiver;
	private String message;
	private LocalDateTime time;
	private Ride ride;
	private Type type;

	public enum Type {
		Support, Ride, Panic
	}

	public User getSender() {
		return sender;
	}

	public void setSender(User sender) {
		this.sender = sender;
	}

	public User getReceiver() {
		return receiver;
	}

	public void setReceiver(User receiver) {
		this.receiver = receiver;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public LocalDateTime getTime() {
		return time;
	}

	public void setTime(LocalDateTime time) {
		this.time = time;
	}

	public Ride getRide() {
		return ride;
	}

	public void setRide(Ride ride) {
		this.ride = ride;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
}
