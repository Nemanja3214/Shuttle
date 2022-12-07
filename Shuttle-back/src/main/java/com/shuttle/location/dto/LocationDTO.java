package com.shuttle.location.dto;

public class LocationDTO {
	private String address;
	private double latitude;
	private double longitude;
	
	
	public LocationDTO() {
		super();
	}
	public LocationDTO(String address, double latitude, double longitude) {
		super();
		this.address = address;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	public static LocationDTO getMock() {
		return new LocationDTO("Bulevar oslobodjenja 46", 45.267136, 19.833549);
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String adress) {
		this.address = adress;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	
}
