package com.shuttle.ride.dto;

import com.shuttle.panic.Cancellation;

import java.time.LocalDateTime;

public class CancellationDTO {
	private String reason;
	private LocalDateTime timeOfRejection;

	public CancellationDTO(Cancellation rej) {
		this.reason = rej.getReason();
		this.timeOfRejection = rej.getTime();
	}

	public CancellationDTO() {
		super();
	}

	public CancellationDTO(String reason, LocalDateTime timeOfRejection) {
		super();
		this.reason = reason;
		this.timeOfRejection = timeOfRejection;
	}

	public static CancellationDTO getMock() {
		return new CancellationDTO("Ride is canceled due to previous problems with the passenger", LocalDateTime.now());
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
