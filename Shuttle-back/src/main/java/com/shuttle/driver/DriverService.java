package com.shuttle.driver;

import java.util.List;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.shuttle.location.Location;
import com.shuttle.location.dto.LocationDTO;
import com.shuttle.vehicle.IVehicleRepository;
import com.shuttle.vehicle.Vehicle;
import com.shuttle.workhours.IWorkHoursService;
import com.shuttle.workhours.WorkHours;

@Service
public class DriverService implements IDriverService {
    @Autowired
	private IDriverRepository driverRepository;
	private IVehicleRepository vehicleRepository;
	
	@Autowired
	public DriverService(IDriverRepository driverRepository, IVehicleRepository vehicleRepository) {
		this.driverRepository = driverRepository;
		this.vehicleRepository = vehicleRepository;
	}
    private IWorkHoursService workHoursService;

	@Override
	public Driver add(Driver driver) {
		return driverRepository.save(driver);
	}

	@Override
	public Driver get(Long id) {
		return driverRepository.findById(id).orElse(null);
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
    @Override
    public Duration getDurationOfWorkInTheLast24Hours(Driver driver) {
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime startOfToday = now.minusHours(24);
        Duration totalWorked = Duration.ZERO;
        for (WorkHours wh : workHoursService.findAllByDriver(driver, startOfToday, now)) {
            if (wh.getFinish() == null) {
                // This is the last one in the list.
                totalWorked = totalWorked.plus(Duration.between(wh.getStart(), now));
            } else {
                totalWorked = totalWorked.plus(Duration.between(wh.getStart(), wh.getFinish()));
            }
            
        }

        return totalWorked;
    }
}
