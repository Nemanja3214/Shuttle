package com.shuttle.ride.dto;

import com.shuttle.driver.Driver;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
}
