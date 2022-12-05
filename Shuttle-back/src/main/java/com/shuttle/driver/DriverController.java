package com.shuttle.driver;

import com.shuttle.passenger.Passenger;
import com.shuttle.vehicle.Vehicle;
import com.shuttle.vehicle.VehicleDTO;
import jakarta.websocket.server.PathParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
public class DriverController {

    @PostMapping("/api/driver")
    public ResponseEntity<DriverDTO> createDriver(@RequestBody Driver driver) {
        driver.setId(Long.valueOf(123));
        return new ResponseEntity<>(driver.parse2DTO(), HttpStatus.OK);
    }

    @GetMapping("/api/driver")
    public ResponseEntity<DriverDataPage> getPaginatedDrivers(@PathParam("page") int page, @PathParam("size") int size) {
        DriverControllerMockProvider driverControllerMockProvider = new DriverControllerMockProvider();
        return new ResponseEntity<>(driverControllerMockProvider.getDriverDataPage(), HttpStatus.OK);
    }

    @GetMapping("/api/driver/{id}")
    public ResponseEntity<DriverDTO> getDriverDetails(@PathVariable(value = "id") Long id) {
        DriverControllerMockProvider driverControllerMockProvider = new DriverControllerMockProvider();
        System.out.println(id);
        return new ResponseEntity<>(driverControllerMockProvider.getDriverData(), HttpStatus.OK);
    }

    @PutMapping("/api/driver/{id}")
    public ResponseEntity<DriverDTO> updateDriver(@RequestBody DriverDTO driver, @PathVariable(value = "id") Long id) {
        return new ResponseEntity<>(driver, HttpStatus.OK);
    }

    @GetMapping("/api/driver/{id}/documents")
    public ResponseEntity<DriverDocumentDTO> getDriverDocs(@PathVariable(value = "id") Long id) {
        DriverControllerMockProvider driverControllerMockProvider = new DriverControllerMockProvider();
        return new ResponseEntity<>(driverControllerMockProvider.gedDriverDocument(), HttpStatus.OK);
    }

    @DeleteMapping("/api/driver/{id}/documents")
    public ResponseEntity<Void> deleteDocsById(@PathVariable(value = "id") Long id) {
        return new ResponseEntity<Void>(HttpStatus.valueOf(204));
    }

    @PostMapping("/api/driver/{id}/documents")
    public ResponseEntity<DriverDocumentDTO> addDocsById(@PathVariable(value = "id") Long id, @RequestBody DriverDocumentDTO driverDocumentDTO) {
        driverDocumentDTO.setId(id);
        return new ResponseEntity<DriverDocumentDTO>(driverDocumentDTO,HttpStatus.valueOf(200));
    }


    @GetMapping("/api/driver/{id}/vehicle")
    public ResponseEntity<VehicleDTO> getVehicle( @PathVariable(value = "id") Long id) {
        VehicleDTO vehicleDTO = new VehicleDTO();
        vehicleDTO.setId(id);
        return new ResponseEntity<>(vehicleDTO, HttpStatus.OK);
    }

    @PostMapping("/api/driver/{id}/vehicle")
    public ResponseEntity<VehicleDTO> createVehicle(@RequestBody VehicleDTO vehicleDTO, @PathVariable(value = "id") Long id) {
        vehicleDTO.setId(id);
        return new ResponseEntity<>(vehicleDTO, HttpStatus.OK);
    }
    @PutMapping("/api/driver/{id}/vehicle")
    public ResponseEntity<VehicleDTO> changeVehicle(@RequestBody VehicleDTO vehicleDTO, @PathVariable(value = "id") Long id) {
        vehicleDTO.setId(id);
        return new ResponseEntity<>(vehicleDTO, HttpStatus.OK);
    }


}

