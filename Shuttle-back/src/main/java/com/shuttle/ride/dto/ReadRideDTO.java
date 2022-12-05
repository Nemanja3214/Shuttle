package com.shuttle.ride.dto;

import lombok.Getter;
import lombok.Setter;

public class ReadRideDTO extends BaseRideDTO {

    private String status;

    @Getter
    @Setter
    private RejectionDTO rejection;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
