package com.shuttle.review;

import com.shuttle.passenger.Passenger;
import com.shuttle.ride.Ride;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Review {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	private Integer rating;
	private String comment;
	@ManyToOne
	private Ride ride;
	@ManyToOne
	private Passenger passenger;

}
