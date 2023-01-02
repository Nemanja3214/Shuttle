package com.shuttle.driver;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.shuttle.location.Location;
import com.shuttle.location.dto.LocationDTO;
import com.shuttle.vehicle.IVehicleRepository;
import com.shuttle.vehicle.Vehicle;

@Service
public class DriverService implements IDriverService {
	private IDriverRepository driverRepository;
	private IVehicleRepository vehicleRepository;
	
	@Autowired
	public DriverService(IDriverRepository driverRepository, IVehicleRepository vehicleRepository) {
		this.driverRepository = driverRepository;
		this.vehicleRepository = vehicleRepository;
	}

	@Override
	public Driver add(Driver driver) {
		return driverRepository.save(driver);
	}

	@Override
	public Optional<Driver> get(Long id) {
		return driverRepository.findById(id);
	}

	@Override
	public Driver setAvailable(Driver driver, boolean available) {
		driver.setAvailable(available);
		driver = driverRepository.save(driver);
		
		return driver;
	}

	@Override
	public List<LocationDTO> getActiveDriversLocations() {
		List<Driver> activeDrivers = driverRepository.findByAvailableTrue();
		List<Vehicle> driversVehicles = vehicleRepository.findByDriverIn(activeDrivers);
		return driversVehicles.stream().map(vehicle -> LocationDTO.from(vehicle.getCurrentLocation())).toList();
	}

	@Override
	public List<Driver> findByAvailableTrue() {
		return this.driverRepository.findByAvailableTrue();
	}
}
