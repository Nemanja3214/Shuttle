package com.example.shuttlemobile.ride.dto;

public class CancellationDTO {
    private String reason;
    private String timeOfRejection;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getTimeOfRejection() {
        return timeOfRejection;
    }

    public void setTimeOfRejection(String timeOfRejection) {
        this.timeOfRejection = timeOfRejection;
    }
}
