package com.shuttle.vehicle;

import com.shuttle.driver.Driver;
import com.shuttle.location.Location;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class Vehicle {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	private String model;
	private String licenseNumber;
	private Integer passengerSeats;
	private Boolean babyTransport;
	private Boolean petTransport;
	@ManyToOne(cascade = CascadeType.ALL)
	private Location currentLocation;
	@ManyToOne(cascade = CascadeType.ALL)
	private VehicleType vehicleType;
	@ManyToOne
	private Driver driver;
}
