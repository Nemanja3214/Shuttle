package com.shuttle.review;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IReviewRepository extends JpaRepository<Review, Long> {
	@Query("select r from Review r join Vehicle v where (v.driver.id = r.ride.driver.id) and (v.id = :vehicleId)")
	List<Review> findByVehicle(Long vehicleId);
}
