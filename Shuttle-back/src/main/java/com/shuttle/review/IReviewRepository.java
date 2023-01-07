package com.shuttle.review;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IReviewRepository extends JpaRepository<Review, Long> {
	@Query("select r from Review r join Vehicle v where (v.driver.id = r.ride.driver.id) and (v.id = :vehicleId) and (r.forDriver = false)")
	List<Review> findByVehicle(Long vehicleId);

	@Query("select r from Review r where (r.ride.driver.id = :driverId) and (r.forDriver = true)")
	List<Review> findByDriver(Long driverId);
}
