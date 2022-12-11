package com.shuttle.location.dto;

public class LocationDTO {
	private String address;
	private Double latitude;
	private Double longitude;
	
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
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
}