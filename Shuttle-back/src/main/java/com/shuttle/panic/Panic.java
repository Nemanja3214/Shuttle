package com.shuttle.panic;

import java.time.LocalDateTime;

import com.shuttle.common.Entity;
import com.shuttle.ride.Ride;
import com.shuttle.user.User;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data
@jakarta.persistence.Entity
public class Panic extends Entity {
	@ManyToOne
	private User user;
	@ManyToOne
	private Ride ride;
	private LocalDateTime time;
	private String reason;


}
