package com.shuttle.location;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface ILocationRepository extends CrudRepository<Location, Long> {
	Optional<Location> findByLatitudeAndLongitude(Double latitude, Double longitude);
}
