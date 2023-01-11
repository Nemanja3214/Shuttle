package com.shuttle.vehicle;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuttle.driver.Driver;
import com.shuttle.driver.IDriverService;
import com.shuttle.location.Location;
import com.shuttle.location.dto.LocationDTO;
import com.shuttle.vehicle.vehicleType.IVehicleTypeRepository;
import com.shuttle.vehicle.vehicleType.VehicleType;

@Service
public class VehicleService implements IVehicleService {
    @Autowired
	private IVehicleRepository vehicleRepository;
    @Autowired
	private IDriverService driverService;
    @Autowired
	private IVehicleTypeRepository vehicleTypeRepository;
	
	@Override
	public Vehicle add(VehicleDTO vehicleDTO) {
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
		return vehicleTypeRepository.findVehicleTypeByNameIgnoreCase(name);
	}
	
	@Override
	public Vehicle findByDriver(Driver driver) {
		return vehicleRepository.findByDriver(driver);
	}

    @Override
    public List<Vehicle> findAllCurrentlyActiveWhoseDriverCanWork() {
        return vehicleRepository.findAllCurrentlyActive()
                .stream()
                .filter(v -> {
                    return !this.driverService.workedMoreThan8Hours(v.getDriver());
                })
                .toList();
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
	@Autowired
	private SimpMessagingTemplate template;
	
//	Simulation of driver moving
	@Scheduled(initialDelay = 2000, fixedDelay = 2000)
	public void simulateLocationChange() {
		
		List<Driver> activeDrivers = this.driverService.findAllActive();
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
		List<LocationDTO> response = this.driverService.getActiveDriversLocations();
		template.convertAndSend("/active/vehicle/location", response);
	}

	@Override
	public Vehicle findById(Long id) {
		return vehicleRepository.findById(id).orElse(null);
	}

	@Override
	public Vehicle update(Vehicle vehicle, VehicleDTO vehicleDTO) throws IllegalArgumentException {
		if (vehicleDTO.getBabyTransport() != null) vehicle.setBabyTransport(vehicleDTO.getBabyTransport());
		if (vehicleDTO.getPetTransport() != null) vehicle.setPetTransport(vehicleDTO.getPetTransport());
		if (vehicleDTO.getCurrentLocation() != null) vehicle.setCurrentLocation(vehicleDTO.getCurrentLocation().to());
		
		VehicleType type = vehicleTypeRepository.findVehicleTypeByNameIgnoreCase(vehicleDTO.getVehicleType()).orElse(null);
		if (type == null) {
			throw new IllegalArgumentException("Unknown Vehicle Type");
		}
		
		vehicle.setVehicleType(type);
		vehicle.setModel(vehicleDTO.getModel());
		vehicle.setLicenseNumber(vehicleDTO.getLicenseNumber());
		vehicle.setPassengerSeats(vehicleDTO.getPassengerSeats());
		
		return vehicle;
	}

	@Override
	public void removeDriver(Vehicle oldVehicle) {
		// TODO Would this mess up any other entities in the database?
		
		oldVehicle.setDriver(null);
		vehicleRepository.save(oldVehicle);		
	}
	
}
