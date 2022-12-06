package com.shuttle.ride.dto;

import java.time.LocalDateTime;
import com.shuttle.ride.Rejection;

public class RejectionDTO {
	public String reason;
	public LocalDateTime timeOfRejection;
	
	public RejectionDTO(Rejection rej) {
		this.reason = rej.getReason();
		this.timeOfRejection = rej.getTimeOfRejection();
	}
}
