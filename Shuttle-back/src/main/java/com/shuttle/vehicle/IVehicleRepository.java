package com.shuttle.vehicle;

import org.springframework.data.jpa.repository.JpaRepository;
import com.shuttle.driver.Driver;

public interface IVehicleRepository extends JpaRepository<Vehicle, Long> {
	public Vehicle findByDriver(Driver driver);
}
