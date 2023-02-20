package com.example.shuttlemobile.ride.dto;

import com.example.shuttlemobile.route.LocationDTO;

import java.io.Serializable;

public class VehicleLocationDTO implements Serializable {
    private Long id;
    private Boolean available;
    private LocationDTO location;
    private Long vehicleTypeId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public LocationDTO getLocation() {
        return location;
    }

    public void setLocation(LocationDTO location) {
        this.location = location;
    }

    public Long getVehicleTypeId() {
        return vehicleTypeId;
    }

    public void setVehicleTypeId(Long vehicleTypeId) {
        this.vehicleTypeId = vehicleTypeId;
    }
}
