package com.shuttle.vehicle;

import com.shuttle.common.Entity;
import com.shuttle.driver.DriverDocument;
import com.shuttle.location.Location;

public class Vehicle extends Entity {
	private DriverDocument driver;
	private Type vehicleType;
	private String model;
	private String licenseNumber;
	private Integer passengerSeats;
	private Boolean babyTransport;
	private Boolean petTransport;
	private Location currentLocation;

	public enum Type {
		STANDARD, LUXURY, VAN
	}

	public DriverDocument getDriver() {
		return driver;
	}

	public void setDriver(DriverDocument driver) {
		this.driver = driver;
	}

	public Type getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(Type vehicleType) {
		this.vehicleType = vehicleType;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getLicenseNumber() {
		return licenseNumber;
	}

	public void setLicenseNumber(String licenseNumber) {
		this.licenseNumber = licenseNumber;
	}

	public Integer getPassengerSeats() {
		return passengerSeats;
	}

	public void setPassengerSeats(Integer passengerSeats) {
		this.passengerSeats = passengerSeats;
	}

	public Boolean getBabyTransport() {
		return babyTransport;
	}

	public void setBabyTransport(Boolean babyTransport) {
		this.babyTransport = babyTransport;
	}

	public Boolean getPetTransport() {
		return petTransport;
	}

	public void setPetTransport(Boolean petTransport) {
		this.petTransport = petTransport;
	}

	public Location getCurrentLocation() {
		return currentLocation;
	}

	public void setCurrentLocation(Location currentLocation) {
		this.currentLocation = currentLocation;
	}
}
