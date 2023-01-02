package com.shuttle.vehicle;

import java.util.List;
import java.util.Optional;

import com.shuttle.driver.Driver;

public interface IVehicleService {
	public Vehicle add(Vehicle vehicle);
	public Vehicle add(VehicleDTO vehicleDTO);
	
	// TODO: Optional<T> or just T?
	
	public Optional<VehicleType> findVehicleTypeByName(String name);

    /**
     * @param driver The driver.
     * @return Vehicle v such that v.driver == driver. Null if none.
     */
	public Vehicle findByDriver(Driver driver);
    public List<Vehicle> findAllCurrentlyActive();
}
