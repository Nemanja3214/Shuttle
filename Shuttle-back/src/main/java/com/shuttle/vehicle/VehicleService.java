package com.shuttle.vehicle;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shuttle.driver.Driver;
import com.shuttle.driver.IDriverService;

@Service
public class VehicleService implements IVehicleService {
	private IVehicleRepository vehicleRepository;
	private IDriverService driverService;
	private IVehicleTypeRepository vehicleTypeRepository;
	
	@Autowired
	public VehicleService(IVehicleRepository vehicleRepository, IDriverService driverService, IVehicleTypeRepository vehicleTypeRepository) {
		this.vehicleRepository = vehicleRepository;
		this.driverService = driverService; 
		this.vehicleTypeRepository = vehicleTypeRepository;
	}
	
	@Override
	public Vehicle add(VehicleDTO vehicleDTO) throws NoSuchElementException {
		Driver d = driverService.get(vehicleDTO.getDriverId());
		VehicleType vehicleType = findVehicleTypeByName(vehicleDTO.getVehicleType()).orElseThrow();
		
		Vehicle vehicle = vehicleDTO.to();
		
		vehicle.setVehicleType(vehicleType);
		vehicle.setDriver(d);
		
		return add(vehicle);
	}
	
	@Override
	public Vehicle add(Vehicle vehicle) {
		return vehicleRepository.save(vehicle);
	}

	@Override
	public Optional<VehicleType> findVehicleTypeByName(String name) {
		return vehicleTypeRepository.findVehicleTypeByName(name);
	}
	
	@Override
	public Vehicle findByDriver(Driver driver) {
		return vehicleRepository.findByDriver(driver);
	}
	
}
