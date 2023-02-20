package com.shuttle.review.dto;

import com.shuttle.review.Review;
import com.shuttle.user.dto.BasicUserInfoDTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReviewDTO {
	private Long id;
	private Integer rating;
	private String comment;
	private BasicUserInfoDTO passenger;

	public ReviewDTO(Review r) {
		if (r == null) {
			return;
		}
		this.id = r.getId();
		this.rating = r.getRating();
		this.comment = r.getComment();
		this.passenger = new BasicUserInfoDTO(r.getPassenger());
	}
}
