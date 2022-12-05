package com.shuttle.review;

public class ReviewDTO {
	public Long id;
	public Integer rating;
	public String comment;
	
	public ReviewDTO(Review r) {
		this.id = r.getId();
		this.rating = r.getRating();
		this.comment = r.getComment();
	}
}
