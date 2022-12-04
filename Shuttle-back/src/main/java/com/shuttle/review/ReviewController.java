package com.shuttle.review;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.shuttle.ride.Ride;

import jakarta.websocket.server.PathParam;

@RestController
public class ReviewController {
	@PostMapping("/api/review/{vehicleId}")
	public ResponseEntity<ReviewDTO> leaveVehicleRating(@PathParam("vehicleId") Long vehicleId, @RequestBody Review review) {
		review.setId(Long.valueOf(123));
		return new ResponseEntity<ReviewDTO>(new ReviewDTO(review), HttpStatus.OK);
	}
	
	// TODO: POST /api/review/{driverId} ambiguity.
	
	@GetMapping("/api/review/{vehicleId}")
	public ResponseEntity<ReviewListDTO> getVehicleRatings(@PathParam("vehicleId") Long vehicleId) {
		List<Review> reviewsMock = new ArrayList<>();
		reviewsMock.add(new Review());
		return new ResponseEntity<ReviewListDTO>(new ReviewListDTO(reviewsMock), HttpStatus.OK);
	}
	
	// TODO: GET /api/review/{driverId} ambiguity.
	
	@GetMapping("/api/reviews/{rideId}")
	public ResponseEntity<ReviewRideDTO> getRideReview(@PathParam("rideId") Long rideId) {
		Ride r = new Ride();
		Review vehicleReview = new Review();
		Review driverReview = new Review();
		return new ResponseEntity<ReviewRideDTO>(new ReviewRideDTO(vehicleReview, driverReview), HttpStatus.OK);
	}
}
