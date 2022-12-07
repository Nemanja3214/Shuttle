package com.shuttle.vehicle;

import com.shuttle.common.Entity;
import com.shuttle.location.Location;
import com.shuttle.location.dto.LocationDTO;
import lombok.Getter;
import lombok.Setter;

public class VehicleDTO extends Entity {
    @Getter
    @Setter
    long driverID;

    @Getter
    @Setter
    String vehicleType;

    @Getter
    @Setter
    String model;

    @Getter
    @Setter
    String licenseNumber;

    @Getter
    @Setter
    LocationDTO currentLocation;

    @Getter
    @Setter
    int passengerSeats;

    @Getter
    @Setter
    boolean babyTransport;

    @Getter
    @Setter
    boolean petTransport;

}
