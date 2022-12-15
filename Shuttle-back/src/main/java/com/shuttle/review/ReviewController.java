package com.shuttle.review;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.shuttle.passenger.Passenger;
import com.shuttle.review.dto.ReviewDTO;
import com.shuttle.review.dto.ReviewListDTO;
import com.shuttle.review.dto.ReviewRideDTO;
import com.shuttle.ride.Ride;

import jakarta.websocket.server.PathParam;

@RestController
public class ReviewController {
	@PostMapping("/api/review/{rideId}/vehicle/{id}")
	public ResponseEntity<ReviewDTO> leaveVehicleRating(@PathParam("id") Long vehicleId, @PathParam("rideId") Long rideId, @RequestBody Review review) {
		review.setId(Long.valueOf(123));
		review.setPassenger(new Passenger());
		review.getPassenger().setId(Long.valueOf(213));
		review.getPassenger().getCredentials().setEmail("dhskjdsh@hskjdhskj");
		return new ResponseEntity<ReviewDTO>(new ReviewDTO(review), HttpStatus.OK);
	}
	
	@PostMapping("/api/review/{rideId}/driver/{id}")
	public ResponseEntity<ReviewDTO> leaveDriverRating(@PathParam("id") Long driverId, @PathParam("rideId") Long rideId, @RequestBody Review review) {
		review.setId(Long.valueOf(123));
		review.setPassenger(new Passenger());
		review.getPassenger().setId(Long.valueOf(213));
		review.getPassenger().getCredentials().setEmail("dhskjdsh@hskjdhskj");
		return new ResponseEntity<ReviewDTO>(new ReviewDTO(review), HttpStatus.OK);
	}
	
	@GetMapping("/api/review/vehicle/{id}")
	public ResponseEntity<ReviewListDTO> getVehicleRatings(@PathParam("id") Long vehicleId) {
		List<Review> reviewsMock = new ArrayList<>();
		Review r = new Review();
		r.setPassenger(new Passenger());
		r.setId(Long.valueOf(123));
		r.setPassenger(new Passenger());
		r.getPassenger().setId(Long.valueOf(213));
		r.getPassenger().getCredentials().setEmail("dhskjdsh@hskjdhskj");
		r.setRating(9);
		r.setComment("fhekjfhewkjrhewjkr32hf");
		reviewsMock.add(r);
		return new ResponseEntity<ReviewListDTO>(new ReviewListDTO(reviewsMock), HttpStatus.OK);
	}
	
	@GetMapping("/api/review/driver/{id}")
	public ResponseEntity<ReviewListDTO> getDriverRatings(@PathParam("id") Long vehicleId) {
		List<Review> reviewsMock = new ArrayList<>();
		Review r = new Review();
		r.setPassenger(new Passenger());
		r.setId(Long.valueOf(123));
		r.setPassenger(new Passenger());
		r.getPassenger().setId(Long.valueOf(213));
		r.getPassenger().getCredentials().setEmail("dhskjdsh@hskjdhskj");
		r.setRating(9);
		r.setComment("fhekjfhewkjrhewjkr32hf");
		reviewsMock.add(r);
		return new ResponseEntity<ReviewListDTO>(new ReviewListDTO(reviewsMock), HttpStatus.OK);
	}

	@GetMapping("/api/review/{rideId}")
	public ResponseEntity<ReviewRideDTO> getRideReview(@PathParam("rideId") Long rideId) {
		Ride r = new Ride();
		Review vehicleReview = new Review();
		vehicleReview.setPassenger(new Passenger());
		vehicleReview.setId(Long.valueOf(123));
		vehicleReview.setPassenger(new Passenger());
		vehicleReview.getPassenger().setId(Long.valueOf(213));
		vehicleReview.getPassenger().getCredentials().setEmail("dhskjdsh@hskjdhskj");
		vehicleReview.setRating(9);
		vehicleReview.setComment("fhekjfhewkjrhewjkr32hf");
		Review driverReview = vehicleReview;
		return new ResponseEntity<ReviewRideDTO>(new ReviewRideDTO(vehicleReview, driverReview), HttpStatus.OK);
	}
}
