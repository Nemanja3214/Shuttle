package com.shuttle.location;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
public interface ILocationRepository extends JpaRepository<Location, Long> {
	Optional<Location> findByLatitudeAndLongitude(Double latitude, Double longitude);
}
