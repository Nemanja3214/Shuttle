package com.shuttle.vehicle;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.shuttle.driver.Driver;
import com.shuttle.driver.IDriverService;
import com.shuttle.location.Location;
import com.shuttle.location.dto.LocationDTO;
import com.shuttle.vehicle.vehicleType.IVehicleTypeRepository;
import com.shuttle.vehicle.vehicleType.VehicleType;

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
		Driver d = driverService.get(vehicleDTO.getDriverId()).orElseThrow();
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

	@Override
	public List<String> getAllVehicleTypesNames() {
		return vehicleTypeRepository.findAll().stream().map(x -> x.getName()).collect(Collectors.toList());
	}

	@Override
	public boolean changeCurrentLocation(long vehicleId, LocationDTO location) {
		Optional<Vehicle> vehicle = this.vehicleRepository.findById(vehicleId);
		if(vehicle.isPresent()) {
			vehicle.get().setCurrentLocation(location.to());
			this.vehicleRepository.save(vehicle.get());
			return true;
		}
		else {
			return false;
		}		
	}
	
//	Simulation of driver moving
	@Scheduled(initialDelay = 2000, fixedDelay = 2000)
	public void simulateLocationChange() {
		List<Driver> activeDrivers = this.driverService.findByAvailableTrue();
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
