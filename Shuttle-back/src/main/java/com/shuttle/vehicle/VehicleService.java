package com.shuttle.vehicle;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shuttle.driver.Driver;
import com.shuttle.driver.IDriverService;

@Service
public class VehicleService implements IVehicleService {
	private IVehicleRepository vehicleRepository;
	private IDriverService driverService;
	
	@Autowired
	public VehicleService(IVehicleRepository vehicleRepository, IDriverService driverService) {
		this.vehicleRepository = vehicleRepository;
		this.driverService = driverService; 
	}
	
	@Override
	public Vehicle add(VehicleDTO vehicleDTO) throws NoSuchElementException {
		Driver d = driverService.get(vehicleDTO.getDriverId()).orElseThrow();
		Vehicle vehicle = vehicleDTO.to();
		vehicle.setDriver(d);
		return add(vehicle);
	}
	
	@Override
	public Vehicle add(Vehicle vehicle) {
		return vehicleRepository.save(vehicle);
	}
	
}
