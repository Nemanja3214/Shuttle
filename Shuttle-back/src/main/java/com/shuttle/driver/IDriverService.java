package com.shuttle.driver;

import java.util.Optional;

public interface IDriverService {
	public Driver add(Driver driver);
	public Optional<Driver> get(Long id);
	
	/**
	 * Set the availability of the driver.
	 * @param driver The driver.
	 * @param available The new availability.
	 * @return The driver.
	 */
	public Driver setAvailable(Driver driver, boolean available);
}
