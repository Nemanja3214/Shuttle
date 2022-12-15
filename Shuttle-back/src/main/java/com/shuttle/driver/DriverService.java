package com.shuttle.driver;

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
	
}
