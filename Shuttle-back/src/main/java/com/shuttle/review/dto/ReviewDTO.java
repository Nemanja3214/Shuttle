package com.shuttle.review.dto;

import com.shuttle.review.Review;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReviewDTO {
	private Long id;
	private Integer rating;
	private String comment;
	private ReviewPassengerDTO passenger;
	
	public ReviewDTO(Review r) {
		this.id = r.getId();
		this.rating = r.getRating();
		this.comment = r.getComment();
		this.passenger = new ReviewPassengerDTO(r.getPassenger());
	}
}
