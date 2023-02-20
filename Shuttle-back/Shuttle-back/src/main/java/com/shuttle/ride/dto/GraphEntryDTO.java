package com.shuttle.ride.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GraphEntryDTO {
	private String time;
	private Long numberOfRides;
	private Double costSum;
	private Double length;
	
	public GraphEntryDTO(String time, Long numberOfRides, Double costSum, Double length) {
		this.time = time;
		this.numberOfRides = numberOfRides;
		this.costSum = costSum;
		this.length = length;
	}
	
}
