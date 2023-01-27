package com.shuttle.ride.dto;

import com.shuttle.driver.Driver;

public class RideDriverDTO {
	public Long id;
	public String email;

    /**
     * Map Driver to RideDriverDTO
     * @param driver Driver object, can be null.
     */
	public RideDriverDTO(Driver driver) {
        if (driver == null) {
            this.id = null;
            this.email = null;
        } else {
            this.id = driver.getId();
            this.email = driver.getEmail();
        }
	}
	
	public RideDriverDTO() {
		
	}
}
