package com.shuttle.review;

import java.util.List;

import com.shuttle.driver.Driver;
import com.shuttle.passenger.Passenger;
import com.shuttle.review.dto.ReviewMinimalDTO;
import com.shuttle.ride.Ride;
import com.shuttle.ride.RideController;
import com.shuttle.vehicle.Vehicle;

public interface IReviewService {
	Review save(ReviewMinimalDTO rDTO, Passenger creator, Ride ride);
	Review save(Review r);
	Review findById(Review id);
	List<Review> findByVehicle(Vehicle v); 
	List<Review> findByDriver(Driver d); 
	List<Review> findByRide(Ride d); 
}
