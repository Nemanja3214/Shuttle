package com.shuttle.vehicle;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IVehicleTypeRepository extends JpaRepository<VehicleType, Long> {
	public Optional<VehicleType> findVehicleTypeByName(String name);
}
