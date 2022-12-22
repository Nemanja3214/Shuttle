package com.shuttle.ride.cancellation.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.shuttle.ride.cancellation.Cancellation;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CancellationDTO {
	private String reason;
	private String timeOfRejection;

	public CancellationDTO(Cancellation rej) {
		this.reason = rej.getReason();
		this.timeOfRejection = rej.getTime().toString();
	}

	public CancellationDTO(String reason, LocalDateTime timeOfRejection) {
		this.reason = reason;
		this.timeOfRejection = timeOfRejection.format(DateTimeFormatter.ISO_DATE_TIME);
	}

	public static CancellationDTO getMock() {
		return new CancellationDTO("Ride is canceled due to previous problems with the passenger", LocalDateTime.now());
	}
}
