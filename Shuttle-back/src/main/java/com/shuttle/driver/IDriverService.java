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
     * Get duration of work done today (from 00:00:00 to now).
     * @param driver The driver
     * @return Duration of worktime.
     */
    public Duration getDurationOfWorkToday(Driver driver);
}
