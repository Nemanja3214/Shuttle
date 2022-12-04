package com.shuttle.review;

import java.util.Collection;
import java.util.List;

public class ReviewListDTO {
	public Integer totalCount;
	public List<ReviewDTO> results;
	
	public ReviewListDTO(Collection<Review> reviews) {
		results = reviews.stream().map(r -> new ReviewDTO(r)).toList();
		totalCount = results.size();
	}
}
