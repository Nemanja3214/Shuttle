package com.shuttle.review;

import com.shuttle.common.Entity;
import com.shuttle.passenger.Passenger;
import com.shuttle.ride.Ride;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
@jakarta.persistence.Entity
@Data
public class Review extends Entity {
	private Integer rating;
	private String comment;
	@ManyToOne
	private Ride ride;
	@ManyToOne
	private Passenger passenger;

}
