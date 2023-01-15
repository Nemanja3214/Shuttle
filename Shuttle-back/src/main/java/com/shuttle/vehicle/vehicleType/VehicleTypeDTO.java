package com.shuttle.vehicle.vehicleType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleTypeDTO {
	private Long id;
    private String name;
    private Double pricePerKM;
}
