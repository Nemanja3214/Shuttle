package com.example.shuttlemobile.message;

import java.io.Serializable;

public class SendMessageDTO implements Serializable {
    private String message;
    private String type;
    private Long rideId;

    public SendMessageDTO() {
    }

    public SendMessageDTO(String message, String type, Long rideId) {
        this.message = message;
        this.type = type;
        this.rideId = rideId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    @Override
    public String toString() {
        return "SendMessageDTO{" +
                ", message='" + message + '\'' +
                ", type='" + type + '\'' +
                ", rideId=" + rideId +
                '}';
    }
}
