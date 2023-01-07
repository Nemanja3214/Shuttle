package com.shuttle.review;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.shuttle.common.RESTError;
import com.shuttle.driver.IDriverService;
import com.shuttle.passenger.Passenger;
import com.shuttle.review.dto.ReviewDTO;
import com.shuttle.review.dto.ReviewListDTO;
import com.shuttle.review.dto.ReviewMinimalDTO;
import com.shuttle.review.dto.ReviewRideDTO;
import com.shuttle.ride.IRideService;
import com.shuttle.ride.Ride;
import com.shuttle.user.GenericUser;
import com.shuttle.user.UserService;
import com.shuttle.vehicle.IVehicleRepository;
import com.shuttle.vehicle.IVehicleService;
import com.shuttle.vehicle.Vehicle;

import jakarta.websocket.server.PathParam;

@RestController
public class ReviewController {
	@Autowired
	private IRideService rideService;
	@Autowired
	private IVehicleService vehicleService;
	@Autowired
	private IDriverService driverService;
    @Autowired
    private UserService userService;
    @Autowired
    private IReviewService reviewService;
	
	@PreAuthorize("hasAnyAuthority('passenger')")
	@PostMapping("/api/review/{rideId}/vehicle")
	public ResponseEntity<?> leaveVehicleRating(@PathVariable("rideId") Long rideId, @RequestBody ReviewMinimalDTO reviewDTO) {
		if (rideId == null) {
			return new ResponseEntity<RESTError>(new RESTError("Field rideId is required!"), HttpStatus.BAD_REQUEST);
		}
		
		Ride r = rideService.findById(rideId);
		
		if (r == null) {
			return new ResponseEntity<RESTError>(new RESTError("Ride does not exist!"), HttpStatus.NOT_FOUND);
		}
		if (r.getDriver() == null) {
			return new ResponseEntity<RESTError>(new RESTError("Driver does not exist!"), HttpStatus.NOT_FOUND);
		}
		
		Vehicle v = vehicleService.findByDriver(r.getDriver());
		
		if (v == null) {
			return new ResponseEntity<RESTError>(new RESTError("Vehicle does not exist!"), HttpStatus.NOT_FOUND);
		}
		
		final GenericUser user = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        if (userService.isPassenger(user)) {
	    	if (r.getPassengers().stream().noneMatch(p -> p.getId().equals(user.getId()))) {
                return new ResponseEntity<RESTError>(new RESTError("Ride does not exist!"), HttpStatus.NOT_FOUND);
	    	}
	    }
        
        final Review review = reviewService.save(reviewDTO, (Passenger)user, r, false);
		return new ResponseEntity<ReviewDTO>(new ReviewDTO(review), HttpStatus.OK);
	}
	
	@PreAuthorize("hasAnyAuthority('passenger')")
	@PostMapping("/api/review/{rideId}/driver")
	public ResponseEntity<?> leaveDriverRating(@PathVariable("rideId") Long rideId, @RequestBody ReviewMinimalDTO reviewDTO) {
		if (rideId == null) {
			return new ResponseEntity<RESTError>(new RESTError("Field rideId is required!"), HttpStatus.BAD_REQUEST);
		}
		
		Ride r = rideService.findById(rideId);
		
		if (r == null) {
			return new ResponseEntity<RESTError>(new RESTError("Ride does not exist!"), HttpStatus.NOT_FOUND);
		}
		if (r.getDriver() == null) {
			return new ResponseEntity<RESTError>(new RESTError("Driver does not exist!"), HttpStatus.NOT_FOUND);
		}
		
		Vehicle v = vehicleService.findByDriver(r.getDriver());
		
		if (v == null) {
			return new ResponseEntity<RESTError>(new RESTError("Vehicle does not exist!"), HttpStatus.NOT_FOUND);
		}
		
		final GenericUser user = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        if (userService.isPassenger(user)) {
	    	if (r.getPassengers().stream().noneMatch(p -> p.getId().equals(user.getId()))) {
                return new ResponseEntity<RESTError>(new RESTError("Ride does not exist!"), HttpStatus.NOT_FOUND);
	    	}
	    }
        
        final Review review = reviewService.save(reviewDTO, (Passenger)user, r, true);
		return new ResponseEntity<ReviewDTO>(new ReviewDTO(review), HttpStatus.OK);
	}
	
	@GetMapping("/api/review/vehicle/{id}")
	public ResponseEntity<ReviewListDTO> getVehicleRatings(@PathVariable("id") Long vehicleId) {
		List<Review> reviewsMock = new ArrayList<>();
		Review r = new Review();
		r.setPassenger(new Passenger());
		r.setId(Long.valueOf(123));
		r.setPassenger(new Passenger());
		r.getPassenger().setId(Long.valueOf(213));
		r.getPassenger().setEmail("dhskjdsh@hskjdhskj");
		r.setRating(9);
		r.setComment("fhekjfhewkjrhewjkr32hf");
		reviewsMock.add(r);
		return new ResponseEntity<ReviewListDTO>(new ReviewListDTO(reviewsMock), HttpStatus.OK);
	}
	
	@GetMapping("/api/review/driver/{id}")
	public ResponseEntity<ReviewListDTO> getDriverRatings(@PathVariable("id") Long vehicleId) {
		List<Review> reviewsMock = new ArrayList<>();
		Review r = new Review();
		r.setPassenger(new Passenger());
		r.setId(Long.valueOf(123));
		r.setPassenger(new Passenger());
		r.getPassenger().setId(Long.valueOf(213));
		r.getPassenger().setEmail("dhskjdsh@hskjdhskj");
		r.setRating(9);
		r.setComment("fhekjfhewkjrhewjkr32hf");
		reviewsMock.add(r);
		return new ResponseEntity<ReviewListDTO>(new ReviewListDTO(reviewsMock), HttpStatus.OK);
	}

	@GetMapping("/api/review/{rideId}")
	public ResponseEntity<ReviewRideDTO> getRideReview(@PathVariable("rideId") Long rideId) {
		Ride r = new Ride();
		Review vehicleReview = new Review();
		vehicleReview.setPassenger(new Passenger());
		vehicleReview.setId(Long.valueOf(123));
		vehicleReview.setPassenger(new Passenger());
		vehicleReview.getPassenger().setId(Long.valueOf(213));
		vehicleReview.getPassenger().setEmail("dhskjdsh@hskjdhskj");
		vehicleReview.setRating(9);
		vehicleReview.setComment("fhekjfhewkjrhewjkr32hf");
		Review driverReview = vehicleReview;
		return new ResponseEntity<ReviewRideDTO>(new ReviewRideDTO(vehicleReview, driverReview), HttpStatus.OK);
	}
}
