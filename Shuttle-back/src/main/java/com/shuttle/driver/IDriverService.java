package com.shuttle.driver;

import java.util.List;

import com.shuttle.driver.dto.DriverStatDTO;
import com.shuttle.vehicle.VehicleAdminHomeDTO;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.time.Duration;

import org.springframework.data.domain.Pageable;

import com.shuttle.ProfileChangeRequest.ProfileChangeRequest;
import com.shuttle.driver.dto.DriverDTO;
import com.shuttle.driver.dto.DriverUpdateDTO;
import com.shuttle.location.dto.LocationDTO;

public interface IDriverService {
	public Driver add(Driver driver);
	public Driver create(DriverDTO dto) throws IOException;
	public Driver get(Long id);
	
	/**
	 * Set the availability of the driver.
	 * @param driver The driver.
	 * @param available The new availability.
	 * @return The driver.
	 */
	public Driver setAvailable(Driver driver, boolean available);
	public List<LocationDTO> getActiveDriversLocations();
	public List<Driver> findAllActive();
    /**
     * Get duration of work done in the last 24 hours.
     * @param driver The driver
     * @return Duration of worktime.
     */
    public Duration getDurationOfWorkInTheLast24Hours(Driver driver);
        
    /**
     * Check if driver worked more than 8 hours in the last 24 hours.
     * @param d The driver
     * @return True if so.
     */
    public boolean workedMoreThan8Hours(Driver d);
    
	public List<Driver> findAll(Pageable pageable);
	public Driver update(Driver driver, DriverUpdateDTO dto) throws IOException;
	DriverStatDTO getDriverStatistics(Driver driver, String scope);
	public ProfileChangeRequest requestUpdate(Driver driver, DriverUpdateDTO dto);
	public ProfileChangeRequest getProfileChange(Long id);
	public Driver applyRequest(ProfileChangeRequest request) throws IOException;
}
