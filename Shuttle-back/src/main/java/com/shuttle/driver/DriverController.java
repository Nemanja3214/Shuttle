package com.shuttle.driver;

import com.shuttle.common.ListDTO;
import com.shuttle.ride.dto.RideDTO;
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
        return new ResponseEntity<>(driverControllerMockProvider.getDriverDocument(), HttpStatus.OK);
    }

    @DeleteMapping("/api/driver/document/{document-id}")
    public ResponseEntity<Void> deleteDocsById(@PathVariable(value = "document-id") Long documentId) {
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/api/driver/{id}/documents")
    public ResponseEntity<DriverDocumentDTO> addDocsById(@PathVariable(value = "id") Long id, @RequestBody DriverDocumentDTO driverDocumentDTO) {
        driverDocumentDTO.setId(id);
        return new ResponseEntity<DriverDocumentDTO>(driverDocumentDTO, HttpStatus.valueOf(200));
    }


    @GetMapping("/api/driver/{id}/vehicle")
    public ResponseEntity<VehicleDTO> getVehicle(@PathVariable(value = "id") Long id) {
        VehicleDTO vehicleDTO = new DriverControllerMockProvider().getDriverVehicleDTO(id);
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

    @GetMapping("/api/driver/{id}/working-hour")
    public ResponseEntity<ListDTO<WorkHours>> getWorkHoursHistory(@PathVariable(value = "id") Long id,
                                                                  @PathParam("page") int page, @PathParam("size") int size,
                                                                  @PathParam("from") String from, @PathParam("to") String to) {
        ListDTO<WorkHours> workHoursListDTO = new ListDTO<>();
        workHoursListDTO.setTotalCount(page);
        return new ResponseEntity<>(workHoursListDTO, HttpStatus.OK);

    }


    @PostMapping("/api/driver/{id}/working-hour")
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
    public ResponseEntity<ListDTO<RideDTO>> getRideHistory(@PathVariable(value = "id") Long id,
                                                           @PathParam("page") int page, @PathParam("size") int size,
                                                           @PathParam("from") String from, @PathParam("to") String to,
                                                           @PathParam("to") String sort) {
        ListDTO<RideDTO> rideDTOListDTO = new ListDTO<>();
        rideDTOListDTO.setTotalCount(page);
        List<RideDTO> rideDTOList = new ArrayList<>();
        rideDTOList.add(new RideDTO());
        rideDTOListDTO.setResults(rideDTOList);
        return new ResponseEntity<>(rideDTOListDTO, HttpStatus.OK);

    }

}

