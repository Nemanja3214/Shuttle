package com.shuttle.ride.dto;

public class ReadRideDTO extends BaseRideDTO{

	private String status;
	private RejectionDTO rejection;

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	

}
