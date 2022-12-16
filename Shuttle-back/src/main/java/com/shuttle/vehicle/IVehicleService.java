package com.shuttle.vehicle;

import java.util.Optional;

public interface IVehicleService {
	public Vehicle add(Vehicle vehicle);
	public Vehicle add(VehicleDTO vehicleDTO);
	
	public Optional<VehicleType> findVehicleTypeByName(String name);
}
