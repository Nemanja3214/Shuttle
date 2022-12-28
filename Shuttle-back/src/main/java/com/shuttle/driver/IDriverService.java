package com.shuttle.driver;

import java.time.Duration;

public interface IDriverService {
	public Driver add(Driver driver);
	public Driver get(Long id);
	
	/**
	 * Set the availability of the driver.
	 * @param driver The driver.
	 * @param available The new availability.
	 * @return The driver.
	 */
	public Driver setAvailable(Driver driver, boolean available);

    /**
     * Get duration of work done in the last 24 hours.
     * @param driver The driver
     * @return Duration of worktime.
     */
    public Duration getDurationOfWorkInTheLast24Hours(Driver driver);
}
