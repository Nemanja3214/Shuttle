package com.shuttle.ride.dto;

public class EstimationDTO {
	private long estimatedTimeInMinutes;
	private double estimatedCost;
	
	
	
	public EstimationDTO() {
		super();
	}
	public EstimationDTO(long estimatedTimeInMinutes, double estimatedCost) {
		super();
		this.estimatedTimeInMinutes = estimatedTimeInMinutes;
		this.estimatedCost = estimatedCost;
	}
	public long getEstimatedTimeInMinutes() {
		return estimatedTimeInMinutes;
	}
	public void setEstimatedTimeInMinutes(long estimatedTimeInMinutes) {
		this.estimatedTimeInMinutes = estimatedTimeInMinutes;
	}
	public double getEstimatedCost() {
		return estimatedCost;
	}
	public void setEstimatedCost(double estimatedCost) {
		this.estimatedCost = estimatedCost;
	}

	

}
