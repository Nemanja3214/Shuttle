package com.shuttle.ride;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.shuttle.driver.Driver;

public interface IRideRepository extends CrudRepository<Ride, Long> {
	public List<Ride> findByDriverAndStatus(Driver driver, Ride.Status status);
}
