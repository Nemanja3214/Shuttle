package com.shuttle.review;

public class ReviewRideDTO {
	public ReviewDTO vehicleReview;
	public ReviewDTO driverReview;
	
	public ReviewRideDTO(Review vehicleReview, Review driverReview) {
		this.vehicleReview = new ReviewDTO(vehicleReview);
		this.driverReview = new ReviewDTO(driverReview);
	}
}
