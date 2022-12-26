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
	public List<DriverDTO> getActiveDrivers() {
		return driverRepository.findByAvailableTrue().stream().map(x -> DriverDTO.from(x)).collect(Collectors.toList());
	}
	
//	TODO: remove, this is only for simulation
	@EventListener
    public void appReady(ApplicationReadyEvent event) {
		Driver d = new Driver("Zika", "Zikic", "", "0634141", "Karadjordjeva 1", "zika@email.com", "sifra123",
	    		true, (long) 0, false, new Location(null, "Ruma", 45.007889, 19.822540));

        this.driverRepository.save(d);
    }
}
