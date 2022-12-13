package com.shuttle.vehicle;

import com.shuttle.common.Entity;
import com.shuttle.driver.DriverDocument;
import com.shuttle.location.Location;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@jakarta.persistence.Entity
public class Vehicle extends Entity {

	private String model;
	private String licenseNumber;
	private Integer passengerSeats;
	private Boolean babyTransport;
	private Boolean petTransport;

	@ManyToOne
	private Location currentLocation;
	@ManyToOne
	private VehicleType vehicleType;


}
