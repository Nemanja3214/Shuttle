package com.example.shuttlemobile.vehicle;

import com.example.shuttlemobile.route.LocationDTO;

import java.io.Serializable;

public class VehicleDTO implements Serializable {
    private Long id;
    private Long driverId;
    private String vehicleType;
    private String model;
    private String licenseNumber;
    private LocationDTO currentLocation;
    private Long passengerSeats;
    private Boolean babyTransport;
    private Boolean petTransport;

    public VehicleDTO(Long id, Long driverId, String vehicleType, String model, LocationDTO currentLocation, Long passengerSeats, Boolean babyTransport, Boolean petTransport) {
        this.id = id;
        this.driverId = driverId;
        this.vehicleType = vehicleType;
        this.model = model;
        this.currentLocation = currentLocation;
        this.passengerSeats = passengerSeats;
        this.babyTransport = babyTransport;
        this.petTransport = petTransport;
    }

    public VehicleDTO() {
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public LocationDTO getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(LocationDTO currentLocation) {
        this.currentLocation = currentLocation;
    }

    public Long getPassengerSeats() {
        return passengerSeats;
    }

    public void setPassengerSeats(Long passengerSeats) {
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

    @Override
    public String toString() {
        return "VehicleDTO{" +
                "id=" + id +
                ", driverId=" + driverId +
                ", vehicleType='" + vehicleType + '\'' +
                ", model='" + model + '\'' +
                ", currentLocation=" + currentLocation +
                ", passengerSeats=" + passengerSeats +
                ", babyTransport=" + babyTransport +
                ", petTransport=" + petTransport +
                '}';
    }
}
