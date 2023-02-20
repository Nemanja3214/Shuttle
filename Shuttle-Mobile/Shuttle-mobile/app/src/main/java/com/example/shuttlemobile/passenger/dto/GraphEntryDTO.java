package com.example.shuttlemobile.passenger.dto;

import android.os.Parcelable;

import java.io.Serializable;

public class GraphEntryDTO implements Serializable {
    private String time;
    private Long numberOfRides;
    private Double costSum;
    private Double length;

    public GraphEntryDTO() {
    }

    public GraphEntryDTO(String time, Long numberOfRides, Double costSum, Double length) {
        this.time = time;
        this.numberOfRides = numberOfRides;
        this.costSum = costSum;
        this.length = length;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Long getNumberOfRides() {
        return numberOfRides;
    }

    public void setNumberOfRides(Long numberOfRides) {
        this.numberOfRides = numberOfRides;
    }

    public Double getCostSum() {
        return costSum;
    }

    public void setCostSum(Double costSum) {
        this.costSum = costSum;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }
}