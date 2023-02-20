package com.example.shuttlemobile.passenger.dto;

import com.example.shuttlemobile.route.RouteDTO;
import com.example.shuttlemobile.user.dto.UserEmailDTO;

import java.util.List;

public class FavoriteRouteDTO {
    private long id;
    private String favoriteName;
    private List<RouteDTO> locations;
    private List<UserEmailDTO> passengers;
    private String vehicleType;
    private boolean babyTransport;
    private boolean petTransport;
    private String scheduledTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFavoriteName() {
        return favoriteName;
    }

    public void setFavoriteName(String favoriteName) {
        this.favoriteName = favoriteName;
    }

    public List<RouteDTO> getLocations() {
        return locations;
    }

    public void setLocations(List<RouteDTO> locations) {
        this.locations = locations;
    }

    public List<UserEmailDTO> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<UserEmailDTO> passengers) {
        this.passengers = passengers;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public boolean isBabyTransport() {
        return babyTransport;
    }

    public void setBabyTransport(boolean babyTransport) {
        this.babyTransport = babyTransport;
    }

    public boolean isPetTransport() {
        return petTransport;
    }

    public void setPetTransport(boolean petTransport) {
        this.petTransport = petTransport;
    }

    public String getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(String scheduledTime) {
        this.scheduledTime = scheduledTime;
    }
}
