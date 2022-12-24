package com.shuttle.driver;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	
	
}
