package com.shuttle.driver;

import com.shuttle.common.CollectionDTO;
import com.shuttle.passenger.Passenger;
import com.shuttle.ride.Ride;
import com.shuttle.ride.dto.ReadRideDTO;
import com.shuttle.ride.dto.RideDTO;
import com.shuttle.vehicle.Vehicle;
import com.shuttle.vehicle.VehicleDTO;
import com.shuttle.workhours.WorkHours;
import jakarta.websocket.server.PathParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
public class DriverController {

    @PostMapping("/api/driver")
    public ResponseEntity<DriverDTO> createDriver(@RequestBody Driver driver) {
        driver.setId(Long.valueOf(123));
        return new ResponseEntity<>(DriverDTO.parse2DTO(driver), HttpStatus.OK);
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
        driver.setId(id);
        return new ResponseEntity<>(driver, HttpStatus.OK);
    }

    @GetMapping("/api/driver/{id}/documents")
    public ResponseEntity<DriverDocumentDTO> getDriverDocs(@PathVariable(value = "id") Long id) {
        DriverControllerMockProvider driverControllerMockProvider = new DriverControllerMockProvider();
        return new ResponseEntity<>(driverControllerMockProvider.gedDriverDocument(), HttpStatus.OK);
    }

    @DeleteMapping("/api/driver/{id}/documents")
    public ResponseEntity<Void> deleteDocsById(@PathVariable(value = "id") Long id) {
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/api/driver/{id}/documents")
    public ResponseEntity<DriverDocumentDTO> addDocsById(@PathVariable(value = "id") Long id, @RequestBody DriverDocumentDTO driverDocumentDTO) {
        driverDocumentDTO.setId(id);
        return new ResponseEntity<DriverDocumentDTO>(driverDocumentDTO, HttpStatus.valueOf(200));
    }


    @GetMapping("/api/driver/{id}/vehicle")
    public ResponseEntity<VehicleDTO> getVehicle(@PathVariable(value = "id") Long id) {
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

    @GetMapping("/api/driver/{id}/working-hours")
    public ResponseEntity<CollectionDTO<WorkHours>> getWorkHoursHistory(@PathVariable(value = "id") Long id,
                                                                        @PathParam("page") int page, @PathParam("size") int size,
                                                                        @PathParam("from") String from, @PathParam("to") String to) {
        CollectionDTO<WorkHours> workHoursCollectionDTO = new CollectionDTO<>();
        workHoursCollectionDTO.setTotalCount(page);
        return new ResponseEntity<>(workHoursCollectionDTO, HttpStatus.OK);

    }


    @PostMapping("/api/driver/{id}/working-hours")
    public ResponseEntity<WorkHours> createWorkHours(@PathVariable(value = "id") Long id) {
        WorkHours workHoursCollectionDTO = new WorkHours();
        workHoursCollectionDTO.setId(id);
        return new ResponseEntity<>(workHoursCollectionDTO, HttpStatus.OK);

    }

    @GetMapping("/api/driver/working-hour/{working-hour-id}")
    public ResponseEntity<WorkHours> getWorkHours(@PathVariable(value = "working-hour-id") Long id) {
        WorkHours workHoursCollectionDTO = new WorkHours();
        workHoursCollectionDTO.setId(id);
        return new ResponseEntity<>(workHoursCollectionDTO, HttpStatus.OK);

    }

    @PutMapping("/api/driver/working-hour/{working-hour-id}")
    public ResponseEntity<WorkHours> changeWorkHours(@PathVariable(value = "working-hour-id") Long id) {
        WorkHours workHoursCollectionDTO = new WorkHours();
        workHoursCollectionDTO.setId(id);
        return new ResponseEntity<>(workHoursCollectionDTO, HttpStatus.OK);

    }

    @GetMapping("/api/driver/{id}/ride")
    public ResponseEntity<CollectionDTO<ReadRideDTO>> getRideHistory(@PathVariable(value = "id") Long id,
                                                                     @PathParam("page") int page, @PathParam("size") int size,
                                                                     @PathParam("from") String from, @PathParam("to") String to,
                                                                     @PathParam("to") String sort) {
        CollectionDTO<ReadRideDTO> rideDTOCollectionDTO = new CollectionDTO<>();
        rideDTOCollectionDTO.setTotalCount(page);
        List<ReadRideDTO> rideDTOList = new ArrayList<>();
        rideDTOList.add(new ReadRideDTO());
        rideDTOCollectionDTO.setResults(rideDTOList);
        return new ResponseEntity<>(rideDTOCollectionDTO, HttpStatus.OK);

    }

}

