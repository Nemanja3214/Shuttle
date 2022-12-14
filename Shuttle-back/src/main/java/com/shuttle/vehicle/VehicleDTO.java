package com.shuttle.vehicle;

import com.shuttle.location.dto.LocationDTO;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
public class VehicleDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    long driverId;

    String vehicleType;

    String model;

    String licenseNumber;

    LocationDTO currentLocation;

    int passengerSeats;

    boolean babyTransport;

    boolean petTransport;

}
