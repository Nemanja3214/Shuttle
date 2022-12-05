package com.shuttle.message.dto;

import java.time.ZonedDateTime;

public class MessageDTO {
	private long id;
	private ZonedDateTime timeOfSending;
	private long senderId;
	private long receiverId;
	private String message;
	private String type;
	private long rideId;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public ZonedDateTime getTimeOfSending() {
		return timeOfSending;
	}
	public void setTimeOfSending(ZonedDateTime timeOfSending) {
		this.timeOfSending = timeOfSending;
	}
	public long getSenderId() {
		return senderId;
	}
	public void setSenderId(long senderId) {
		this.senderId = senderId;
	}
	public long getReceiverId() {
		return receiverId;
	}
	public void setReceiverId(long receiverId) {
		this.receiverId = receiverId;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public long getRideId() {
		return rideId;
	}
	public void setRideId(long rideId) {
		this.rideId = rideId;
	}
	
	
	
}
