package com.shuttle.review.dto;

import com.shuttle.review.Review;

public class ReviewDTO {
	public Long id;
	public Integer rating;
	public String comment;
	public ReviewPassengerDTO passenger;
	
	public ReviewDTO(Review r) {
		this.id = r.getId();
		this.rating = r.getRating();
		this.comment = r.getComment();
		this.passenger = new ReviewPassengerDTO(r.getPassenger());
	}
}
