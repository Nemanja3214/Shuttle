package com.shuttle.vehicle;

import org.springframework.data.repository.CrudRepository;

import com.shuttle.driver.Driver;

public interface IVehicleRepository extends CrudRepository<Vehicle, Long> {
	public Vehicle findByDriver(Driver driver);
}
