package com.shuttle.vehicle;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shuttle.driver.Driver;

public interface IVehicleRepository extends JpaRepository<Vehicle, Long> {
	public Vehicle findByDriver(Driver driver);

	public List<Vehicle> findByDriverIn(List<Driver> activeDrivers);
}
