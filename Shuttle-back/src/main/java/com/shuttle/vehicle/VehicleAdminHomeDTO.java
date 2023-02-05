package com.shuttle.vehicle;

import com.shuttle.location.dto.LocationDTO;
import com.shuttle.vehicle.vehicleType.VehicleType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VehicleAdminHomeDTO {

        private Long id;
        private Boolean available;
        private LocationDTO location;
        private String vehicleType;
        private String licencePlate;
        private Boolean panic;

        public VehicleAdminHomeDTO(Vehicle vehicle){
            id = vehicle.getId();
            available = vehicle.getDriver().isAvailable();
            location = LocationDTO.from(vehicle.getCurrentLocation());
            vehicleType = vehicle.getVehicleType().getName();
            licencePlate = vehicle.getLicenseNumber();
        }
}
