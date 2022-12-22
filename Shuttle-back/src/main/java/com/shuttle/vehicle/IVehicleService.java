package com.shuttle.vehicle;

import java.util.Optional;

import com.shuttle.driver.Driver;

public interface IVehicleService {
	public Vehicle add(Vehicle vehicle);
	public Vehicle add(VehicleDTO vehicleDTO);
	
	// TODO: Optional<T> or just T?
	
	public Optional<VehicleType> findVehicleTypeByName(String name);
	public Vehicle findByDriver(Driver driver);
}
