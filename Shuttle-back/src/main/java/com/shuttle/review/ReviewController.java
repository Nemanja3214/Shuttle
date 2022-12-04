package com.shuttle.review;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.websocket.server.PathParam;

@RestController
public class ReviewController {
	@PostMapping("/api/review/{vehicleId}")
	public ResponseEntity<ReviewDTO> leaveVehicleRating(@PathParam("vehicleId") Long vehicleId, @RequestBody Review review) {
		review.setId(Long.valueOf(123));
		return new ResponseEntity<ReviewDTO>(new ReviewDTO(review), HttpStatus.OK);
	}
}
