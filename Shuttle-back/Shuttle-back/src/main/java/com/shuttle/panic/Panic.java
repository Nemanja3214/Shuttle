package com.shuttle.panic;

import java.time.LocalDateTime;

import com.shuttle.ride.Ride;
import com.shuttle.user.GenericUser;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Panic {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	@ManyToOne
	private GenericUser user;
	@ManyToOne
	private Ride ride;
	private LocalDateTime time;
	private String reason;


}
