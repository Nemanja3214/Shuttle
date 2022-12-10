package com.shuttle.vehicle;

import com.shuttle.common.Entity;
import com.shuttle.location.Location;
import com.shuttle.location.dto.LocationDTO;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class VehicleDTO extends Entity {

    long driverId;

    String vehicleType;

    String model;

    String licenseNumber;

    LocationDTO currentLocation;

    int passengerSeats;

    boolean babyTransport;

    boolean petTransport;

}
