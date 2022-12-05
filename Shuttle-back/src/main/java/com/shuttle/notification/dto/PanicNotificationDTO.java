package com.shuttle.notification.dto;

import java.time.ZonedDateTime;
import java.util.Collection;

import com.shuttle.ride.dto.BaseRideDTO;

public class PanicNotificationDTO {
	private long id;
//	UserDTO
	private Collection<BaseRideDTO> notifications; 
	private ZonedDateTime time;
	private String reason;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Collection<BaseRideDTO> getNotifications() {
		return notifications;
	}
	public void setNotifications(Collection<BaseRideDTO> notifications) {
		this.notifications = notifications;
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
