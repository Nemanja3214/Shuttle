package com.shuttle.driver;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.shuttle.driver.dto.DriverDTO;
import com.shuttle.location.Location;
import com.shuttle.location.dto.LocationDTO;

@Service
public class DriverService implements IDriverService {
	private IDriverRepository driverRepository;
	
	@Autowired
	public DriverService(IDriverRepository driverRepository) {
		this.driverRepository = driverRepository;
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
		return driverRepository.findByAvailableTrue().stream().map(x -> LocationDTO.from(x.getCurrentLocation())).collect(Collectors.toList());
	}
	
//	TODO: remove, this is only for simulation
	@EventListener
    public void appReady(ApplicationReadyEvent event) {
		Driver d = new Driver("Zika", "Zikic", "", "0634141", "Karadjordjeva 1", "zika@email.com", "sifra123",
	    		true, (long) 0, false, new Location(null, "Ruma", 45.007889, 19.822540));

        this.driverRepository.save(d);
    }

	@Override
	public boolean changeCurrentLocation(long driverId, LocationDTO location) {
		Optional<Driver> d = this.driverRepository.findById(driverId);
		if(d.isPresent()) {
			d.get().setCurrentLocation(location.to());
			this.driverRepository.save(d.get());
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
}
