package com.shuttle.review;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import com.shuttle.driver.Driver;
import com.shuttle.driver.IDriverService;
import com.shuttle.passenger.Passenger;
import com.shuttle.passenger.PassengerService;
import com.shuttle.review.dto.ReviewDTO;
import com.shuttle.review.dto.ReviewListDTO;
import com.shuttle.review.dto.ReviewMinimalDTO;
import com.shuttle.review.dto.ReviewRideDTO;
import com.shuttle.ride.IRideService;
import com.shuttle.ride.Ride;
import com.shuttle.user.GenericUser;
import com.shuttle.user.UserService;
import com.shuttle.util.MyValidator;
import com.shuttle.util.MyValidatorException;
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
		try {
			MyValidator.validateRequired(reviewDTO.getRating(), "rating");
			MyValidator.validateRequired(reviewDTO.getComment(), "comment");
			
			MyValidator.validateLength(reviewDTO.getComment(), "comment", 500);
			MyValidator.validateRange(Long.valueOf(reviewDTO.getRating().longValue()), "rating", 1L, 10L);
		} catch (MyValidatorException e1) {
			return new ResponseEntity<RESTError>(new RESTError(e1.getMessage()), HttpStatus.BAD_REQUEST);
		}	
		
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
	
	@PreAuthorize("hasAnyAuthority('admin', 'passenger')")
	@PostMapping("/api/review/{rideId}/driver")
	public ResponseEntity<?> leaveDriverRating(@PathVariable("rideId") Long rideId, @RequestBody ReviewMinimalDTO reviewDTO) {
		try {
			MyValidator.validateRequired(reviewDTO.getRating(), "rating");
			MyValidator.validateRequired(reviewDTO.getComment(), "comment");
			
			MyValidator.validateLength(reviewDTO.getComment(), "comment", 500);
			MyValidator.validateRange(Long.valueOf(reviewDTO.getRating().longValue()), "rating", 1L, 10L);
		} catch (MyValidatorException e1) {
			return new ResponseEntity<RESTError>(new RESTError(e1.getMessage()), HttpStatus.BAD_REQUEST);
		}	
		
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
	
	@PreAuthorize("hasAnyAuthority('driver', 'admin')")
	@GetMapping("/api/review/vehicle/{id}")
	public ResponseEntity<?> getVehicleRatings(@PathVariable("id") Long vehicleId) {	
		if (vehicleId == null) {
			return new ResponseEntity<RESTError>(new RESTError("Field vehicleId is required!"), HttpStatus.BAD_REQUEST);
		}
		
		Vehicle v = vehicleService.findById(vehicleId);
		
		if (v == null) {
			return new ResponseEntity<RESTError>(new RESTError("Vehicle does not exist!"), HttpStatus.NOT_FOUND);
		}
		
		List<Review> reviews = reviewService.findByVehicle(v);
		
		final GenericUser user = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());

		if (userService.isAdmin(user)) {
		} else if (userService.isDriver(user)) {
	    	if (!v.getDriver().getId().equals(user.getId())) {
                return new ResponseEntity<RESTError>(new RESTError("Vehicle does not exist!"), HttpStatus.NOT_FOUND);
	    	}
	    }

		return new ResponseEntity<ReviewListDTO>(new ReviewListDTO(reviews), HttpStatus.OK);
	}
	
	@PreAuthorize("hasAnyAuthority('driver', 'admin')")
	@GetMapping("/api/review/driver/{id}")
	public ResponseEntity<?> getDriverRatings(@PathVariable("id") Long driverId) {
		if (driverId == null) {
			return new ResponseEntity<RESTError>(new RESTError("Field driverId is required!"), HttpStatus.BAD_REQUEST);
		}
		
		Driver d = driverService.get(driverId);
		
		if (d == null) {
			return new ResponseEntity<RESTError>(new RESTError("Driver does not exist!"), HttpStatus.NOT_FOUND);
		}
		
		List<Review> reviews = reviewService.findByDriver(d);
		
		final GenericUser user = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());

		if (userService.isAdmin(user)) {
		} else if (userService.isDriver(user)) {
	    	if (!d.getId().equals(user.getId())) {
                return new ResponseEntity<RESTError>(new RESTError("Driver does not exist!"), HttpStatus.NOT_FOUND);
	    	}
	    }

		return new ResponseEntity<ReviewListDTO>(new ReviewListDTO(reviews), HttpStatus.OK);
	}

	@PreAuthorize("hasAnyAuthority('driver', 'admin', 'passenger')")
	@GetMapping("/api/review/{rideId}")
	public ResponseEntity<?> getRideReview(@PathVariable("rideId") Long rideId) {
		if (rideId == null) {
			return new ResponseEntity<RESTError>(new RESTError("Field rideId is required!"), HttpStatus.BAD_REQUEST);
		}
		
		Ride r = rideService.findById(rideId);
		
		if (r == null) {
			return new ResponseEntity<RESTError>(new RESTError("Ride does not exist!"), HttpStatus.NOT_FOUND);
		}
		
		List<Review> reviews = reviewService.findByRide(r);
		
		final GenericUser user = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());

		if (userService.isAdmin(user)) {
		} else if (userService.isDriver(user)) {
	    	if (!r.getDriver().getId().equals(user.getId())) {
                return new ResponseEntity<RESTError>(new RESTError("Ride does not exist!"), HttpStatus.NOT_FOUND);
	    	}
	    } else if (userService.isPassenger(user)) {
	    	if (r.getPassengers().stream().noneMatch(p -> p.getId().equals(user.getId()))) {
                return new ResponseEntity<RESTError>(new RESTError("Ride does not exist!"), HttpStatus.NOT_FOUND);
	    	}
	    }
		
		// The response JSON schema is garbage.
		
		List<Long> passengerIds = new ArrayList<>();
		for (Review rr : reviews) {
			if (!passengerIds.contains(rr.getPassenger().getId())) {
				passengerIds.add(rr.getPassenger().getId());
			}
		}
		
		List<ReviewRideDTO> reviewsResult = new ArrayList<>();
		for (Long pid : passengerIds) {
			Passenger p = (Passenger)userService.findById(pid);
			System.out.println(p.getEmail());
			
			Review vehicleReview = null;
			Review driverReview  = null;
			
			for (Review rr : reviews) {
				if (rr.getPassenger().getId().equals(p.getId())) {
					if (rr.isForDriver() && driverReview == null) {
						driverReview = rr;
					} else if (!rr.isForDriver() && vehicleReview == null) {
						vehicleReview = rr;
					}
				}
			}
			
			final ReviewRideDTO rideReview = new ReviewRideDTO(vehicleReview, driverReview);
			reviewsResult.add(rideReview);
		}
		
		return new ResponseEntity<>(reviewsResult, HttpStatus.OK);
	}
}
