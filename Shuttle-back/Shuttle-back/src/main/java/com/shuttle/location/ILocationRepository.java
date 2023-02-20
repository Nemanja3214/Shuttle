package com.shuttle.location;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
public interface ILocationRepository extends JpaRepository<Location, Long> {
	List<Location> findByLatitudeAndLongitude(Double latitude, Double longitude);
}
