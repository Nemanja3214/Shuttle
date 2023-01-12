package com.example.shuttlemobile.ride.dto;

import com.example.shuttlemobile.route.RouteDTO;
import com.example.shuttlemobile.user.UserEmailDTO;

import java.io.Serializable;
import java.util.List;

public class RideDTO implements Serializable {
    private Long id;
    private String startTime;
    private String endTime;
    private Double totalCost;
    private UserEmailDTO driver;
    private List<UserEmailDTO> passengers;
    private Long estimatedTimeInMinutes;
    private String vehicleType;
    private Boolean babyTransport;
    private Boolean petTransport;
    private RejectionDTO rejection;
    private List<com.example.shuttlemobile.route.RouteDTO> locations;
    private String status;

    public RideDTO() {
    }

    public RideDTO(Long id, String startTime, String endTime, Double totalCost, UserEmailDTO driver, List<UserEmailDTO> passengers, Long estimatedTimeInMinutes, String vehicleType, Boolean babyTransport, Boolean petTransport, RejectionDTO rejection, List<com.example.shuttlemobile.route.RouteDTO> locations, String status) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalCost = totalCost;
        this.driver = driver;
        this.passengers = passengers;
        this.estimatedTimeInMinutes = estimatedTimeInMinutes;
        this.vehicleType = vehicleType;
        this.babyTransport = babyTransport;
        this.petTransport = petTransport;
        this.rejection = rejection;
        this.locations = locations;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    public UserEmailDTO getDriver() {
        return driver;
    }

    public void setDriver(UserEmailDTO driver) {
        this.driver = driver;
    }

    public List<UserEmailDTO> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<UserEmailDTO> passengers) {
        this.passengers = passengers;
    }

    public Long getEstimatedTimeInMinutes() {
        return estimatedTimeInMinutes;
    }

    public void setEstimatedTimeInMinutes(Long estimatedTimeInMinutes) {
        this.estimatedTimeInMinutes = estimatedTimeInMinutes;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
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

    public RejectionDTO getRejection() {
        return rejection;
    }

    public void setRejection(RejectionDTO rejection) {
        this.rejection = rejection;
    }

    public List<com.example.shuttlemobile.route.RouteDTO> getLocations() {
        return locations;
    }

    public void setLocations(List<RouteDTO> locations) {
        this.locations = locations;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "RideDTO{" +
                "id=" + id +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", totalCost=" + totalCost +
                ", driver=" + driver +
                ", passengers=" + passengers +
                ", estimatedTimeInMinutes=" + estimatedTimeInMinutes +
                ", vehicleType='" + vehicleType + '\'' +
                ", babyTransport=" + babyTransport +
                ", petTransport=" + petTransport +
                ", rejection=" + rejection +
                ", locations=" + locations +
                ", status='" + status + '\'' +
                '}';
    }
}