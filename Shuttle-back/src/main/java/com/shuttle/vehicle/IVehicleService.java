package com.shuttle.vehicle;

import java.util.List;
import java.util.Optional;

import com.shuttle.driver.Driver;
import com.shuttle.location.dto.LocationDTO;
import com.shuttle.vehicle.vehicleType.VehicleType;

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
    public List<Vehicle> findAllCurrentlyActiveWhoseDriverCanWork();
	public List<String> getAllVehicleTypesNames();
	public boolean changeCurrentLocation(long id, LocationDTO location);
	public Vehicle findById(Long id);
	public Vehicle update(Vehicle vehicle, VehicleDTO vehicleDTO) throws IllegalArgumentException;
	public void removeDriver(Vehicle oldVehicle);
	List<Vehicle> findAllCurrentlyActive();
}
