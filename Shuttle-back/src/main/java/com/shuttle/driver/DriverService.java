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
	public boolean changeCurrentLocation(long driverId, LocationDTO location) {
		Optional<Driver> driver = this.driverRepository.findById(driverId);
		if(driver.isPresent()) {
			Vehicle vehicle = vehicleRepository.findByDriver(driver.get());
			vehicle.setCurrentLocation(location.to());
			this.vehicleRepository.save(vehicle);
			return true;
		}
		else {
			return false;
		}
		
	}

	@Override
	public List<Driver> findByAvailableTrue() {
		return this.driverRepository.findByAvailableTrue();
	}
	
//	Simulation of driver moving
	@Scheduled(initialDelay = 2000, fixedDelay = 2000)
	public void simulateLocationChange() {
		List<Driver> activeDrivers = this.driverRepository.findByAvailableTrue();
		for(Driver activeDriver : activeDrivers) {
			Vehicle vehicle = vehicleRepository.findByDriver(activeDriver);
			
			Location driverLocation = vehicle.getCurrentLocation();
			Random r = new Random();
			double incrementX = r.nextDouble(-0.001, 0.001);
			double incrementY = r.nextDouble(-0.001, 0.001);
			driverLocation.setLatitude(driverLocation.getLatitude() + incrementX);
			driverLocation.setLongitude(driverLocation.getLongitude() + incrementY);
			
			changeCurrentLocation(activeDriver.getId(), LocationDTO.from(driverLocation)); 
			
		}
	}
}
