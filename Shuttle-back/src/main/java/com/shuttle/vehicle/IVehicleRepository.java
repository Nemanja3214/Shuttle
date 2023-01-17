package com.shuttle.vehicle;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.shuttle.driver.Driver;

public interface IVehicleRepository extends JpaRepository<Vehicle, Long> {
	public Vehicle findByDriver(Driver driver);

    @Query("select v from Vehicle v join v.driver dr where v.driver.active = true")
    public List<Vehicle> findAllCurrentlyActive();
	public List<Vehicle> findByDriverIn(List<Driver> activeDrivers);
}
