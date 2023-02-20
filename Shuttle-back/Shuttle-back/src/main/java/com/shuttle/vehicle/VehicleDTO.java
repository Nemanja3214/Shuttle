package com.shuttle.vehicle;

import com.shuttle.driver.Driver;
import com.shuttle.location.Location;
import com.shuttle.location.dto.LocationDTO;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDTO {
    Long id;
    private Long driverId;
    private String vehicleType;
    private String model;
    private String licenseNumber;
    private LocationDTO currentLocation;
    private Integer passengerSeats;
    private Boolean babyTransport;
    private Boolean petTransport;
    
    /**
     * <b>Warning</b>: This does not update vehicle.driver. Use this exclusively in VehicleService!
     */
    public Vehicle to() {
    	Vehicle v = new Vehicle();
    	v.setId(id);
    	v.setModel(model);
    	v.setLicenseNumber(licenseNumber);
    	
    	if (currentLocation != null) {
    		v.setCurrentLocation(currentLocation.to());
    	} else {
    		v.setCurrentLocation(new Location());
    	}
    	
    	v.setPassengerSeats(passengerSeats);
    	v.setBabyTransport(babyTransport);
    	v.setPetTransport(petTransport);
    	return v;
    }
    
    public static VehicleDTO from(Vehicle v) {
    	return new VehicleDTO(
    			v.getId(),
    			v.getDriver().getId(),
    			v.getVehicleType().getName(),
    			v.getModel(),
    			v.getLicenseNumber(),
    			LocationDTO.from(v.getCurrentLocation()),
    			v.getPassengerSeats(),
    			v.getBabyTransport(),
    			v.getPetTransport()
    	);
    }
}
