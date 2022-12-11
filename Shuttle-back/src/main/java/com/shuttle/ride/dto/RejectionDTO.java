package com.shuttle.ride.dto;

import java.time.LocalDateTime;
import com.shuttle.ride.Rejection;

public class RejectionDTO {
	private String reason;
	private LocalDateTime timeOfRejection;

	public RejectionDTO(Rejection rej) {
		this.reason = rej.getReason();
		this.timeOfRejection = rej.getTimeOfRejection();
	}

	public RejectionDTO() {
		super();
	}

	public RejectionDTO(String reason, LocalDateTime timeOfRejection) {
		super();
		this.reason = reason;
		this.timeOfRejection = timeOfRejection;
	}

	public static RejectionDTO getMock() {
		return new RejectionDTO("Ride is canceled due to previous problems with the passenger", LocalDateTime.now());
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public LocalDateTime getTimeOfRejection() {
		return timeOfRejection;
	}

	public void setTimeOfRejection(LocalDateTime timeOfRejection) {
		this.timeOfRejection = timeOfRejection;
	}
}