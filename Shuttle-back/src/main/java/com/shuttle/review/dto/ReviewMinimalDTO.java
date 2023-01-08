package com.shuttle.review.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewMinimalDTO {
	private Integer rating;
	private String comment;
}
