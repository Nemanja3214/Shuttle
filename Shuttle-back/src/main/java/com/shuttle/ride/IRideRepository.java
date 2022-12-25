package com.shuttle.ride;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shuttle.driver.Driver;

public interface IRideRepository extends JpaRepository<Ride, Long> {
	public List<Ride> findByDriverAndStatus(Driver driver, Ride.Status status);
}
