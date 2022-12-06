package com.shuttle.review.dto;

import java.util.Collection;
import java.util.List;

import com.shuttle.review.Review;

public class ReviewListDTO {
	public Integer totalCount;
	public List<ReviewDTO> results;
	
	public ReviewListDTO(Collection<Review> reviews) {
		results = reviews.stream().map(r -> new ReviewDTO(r)).toList();
		totalCount = results.size();
	}
}
