package com.shuttle.review;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shuttle.driver.Driver;
import com.shuttle.passenger.Passenger;
import com.shuttle.review.dto.ReviewMinimalDTO;
import com.shuttle.ride.Ride;
import com.shuttle.vehicle.IVehicleRepository;
import com.shuttle.vehicle.Vehicle;

@Service
public class ReviewService implements IReviewService {
	@Autowired
	private IReviewRepository reviewRepository;

	@Override
	public Review save(Review r) {
		return reviewRepository.save(r);
	}

	@Override
	public Review save(ReviewMinimalDTO rDTO, Passenger creator, Ride ride, boolean forDriver) {
		Review review = new Review();
		review.setComment(rDTO.getComment());
		review.setRating(rDTO.getRating());
		review.setRide(ride);
		review.setPassenger(creator);
		review.setForDriver(forDriver);
		return save(review);
	}

	@Override
	public Review findById(Review id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Review> findByVehicle(Vehicle v) {
		return reviewRepository.findByVehicle(v.getId());
	}

	@Override
	public List<Review> findByDriver(Driver d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Review> findByRide(Ride d) {
		// TODO Auto-generated method stub
		return null;
	}
}
