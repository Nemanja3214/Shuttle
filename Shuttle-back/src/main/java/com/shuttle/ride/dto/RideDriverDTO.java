package com.shuttle.ride.dto;

import com.shuttle.driver.Driver;

public class RideDriverDTO {
	public Long id;
	public String email;
	public final String type = "DRIVER";
	
	public RideDriverDTO(Driver driver) {
		this.id = driver.getId();
		this.email = driver.getEmail();
	}
}
