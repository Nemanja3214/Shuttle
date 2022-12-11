package com.shuttle.review;

import com.shuttle.common.Entity;
import com.shuttle.passenger.Passenger;
import com.shuttle.ride.Ride;

public class Review extends Entity {
	private Integer rating;
	private String comment;
	private Ride ride;
	private Passenger passenger;

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Ride getRide() {
		return ride;
	}

	public void setRide(Ride ride) {
		this.ride = ride;
	}

	public Passenger getPassenger() {
		return passenger;
	}

	public void setPassenger(Passenger passenger) {
		this.passenger = passenger;
	}
}
