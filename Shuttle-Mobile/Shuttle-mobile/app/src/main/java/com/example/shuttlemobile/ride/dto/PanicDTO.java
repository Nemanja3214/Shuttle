package com.example.shuttlemobile.ride.dto;

public class PanicDTO {
    private String reason;

    public PanicDTO(String reason) {
        this.reason = reason;
    }

    public PanicDTO() {
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "RejectionDTOMinimal{" +
                "reason='" + reason + '\'' +
                '}';
    }
}
