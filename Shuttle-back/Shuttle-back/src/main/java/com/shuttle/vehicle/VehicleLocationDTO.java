package com.shuttle.vehicle;

import com.shuttle.location.dto.LocationDTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VehicleLocationDTO {
    private Long id;
    private Boolean available;
    private LocationDTO location;
    private Long vehicleTypeId;

    public VehicleLocationDTO(Vehicle vehicle){
        id = vehicle.getId();
        available = vehicle.getDriver().isAvailable();
        location = LocationDTO.from(vehicle.getCurrentLocation());
        vehicleTypeId = vehicle.getVehicleType().getId();
    }
}
