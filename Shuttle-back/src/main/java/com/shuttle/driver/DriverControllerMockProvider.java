package com.shuttle.driver;

import com.shuttle.driver.dto.DriverDTO;
import com.shuttle.driver.dto.DriverDataPageDTO;
import com.shuttle.driver.dto.DriverDocumentDTO;
import com.shuttle.location.dto.LocationDTO;
import com.shuttle.vehicle.VehicleDTO;

public class DriverControllerMockProvider {
    public DriverDataPageDTO getDriverDataPageDTO() {
    	DriverDataPageDTO driverDataPage = new DriverDataPageDTO(243);
        driverDataPage.addResult(getDriverData());
        return driverDataPage;
    }

    public DriverDTO getDriverData() {
        return new DriverDTO(Long.parseLong("123"), "Pera", "Peric", "U3dhZ2dlciByb2Nrcw==",
                "+381123123", "Bulevar Oslobodjenja 74", "pera.peric@email.com", "123456");
    }
    public DriverDocumentDTO getDriverDocument(){
        DriverDocumentDTO driverDocument = new DriverDocumentDTO();
        driverDocument.setDriverId(Long.parseLong("10"));
        driverDocument.setName("Vozačka dozvola");
        driverDocument.setDocumentImage("U3dhZ2dlciByb2Nrcw=");
        driverDocument.setId(Long.parseLong("123"));
        return driverDocument;
    }

    public VehicleDTO getDriverVehicleDTO(Long id){
        VehicleDTO vehicleDTO = new VehicleDTO();
        vehicleDTO.setDriverId(id);
        vehicleDTO.setId(123L);
        vehicleDTO.setModel("VW Golf 2");
        vehicleDTO.setVehicleType("STANDARDNO");
        vehicleDTO.setLicenseNumber("NS 123-AB");
        vehicleDTO.setBabyTransport(true);
        vehicleDTO.setPetTransport(true);
        vehicleDTO.setPassengerSeats(4);
        LocationDTO locationDTO = new LocationDTO();
        locationDTO.setAddress("Bulevar oslobodjenja 46");
        locationDTO.setLatitude(45.267136);
        locationDTO.setLongitude(19.833549);
        vehicleDTO.setCurrentLocation(locationDTO);
        return vehicleDTO;
    }
}
