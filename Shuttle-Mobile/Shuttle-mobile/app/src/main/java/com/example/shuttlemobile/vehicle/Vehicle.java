package com.example.shuttlemobile.vehicle;

import com.example.shuttlemobile.common.Entity;
import com.example.shuttlemobile.driver.Driver;

public class Vehicle extends Entity {
    private String registrationPlates;
    private Driver driver;

    public String getRegistrationPlates() {
        return registrationPlates;
    }

    public void setRegistrationPlates(String registrationPlates) {
        this.registrationPlates = registrationPlates;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }
}
