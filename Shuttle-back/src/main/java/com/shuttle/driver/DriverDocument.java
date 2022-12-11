package com.shuttle.driver;

import com.shuttle.common.Entity;

public class DriverDocument extends Entity {
	private String name;
	private String image;
	private Driver driver;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Driver getDriver() {
		return driver;
	}

	public void setDriver(Driver driver) {
		this.driver = driver;
	}
}
