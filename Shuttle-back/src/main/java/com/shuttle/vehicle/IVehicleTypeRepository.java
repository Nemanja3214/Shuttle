package com.shuttle.vehicle;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface IVehicleTypeRepository extends CrudRepository<VehicleType, Long> {
	public Optional<VehicleType> findVehicleTypeByName(String name);
}
