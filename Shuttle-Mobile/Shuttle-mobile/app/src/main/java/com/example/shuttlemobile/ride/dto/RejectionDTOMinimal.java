package com.example.shuttlemobile.ride.dto;

import java.io.Serializable;

public class RejectionDTOMinimal implements Serializable {
    private String reason;

    public RejectionDTOMinimal(String reason) {
        this.reason = reason;
    }

    public RejectionDTOMinimal() {
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
