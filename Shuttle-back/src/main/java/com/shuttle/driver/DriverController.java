package com.shuttle.driver;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.shuttle.common.ListDTO;
import com.shuttle.driver.dto.DriverDTO;
import com.shuttle.driver.dto.DriverDataPageDTO;
import com.shuttle.driver.dto.DriverDocumentDTO;
import com.shuttle.ride.IRideRepository;
import com.shuttle.ride.Ride;
import com.shuttle.ride.dto.RideDTO;
import com.shuttle.vehicle.VehicleDTO;
import com.shuttle.workhours.WorkHours;
import jakarta.annotation.security.PermitAll;
import jakarta.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@CrossOrigin
@RestController
public class DriverController {

    @Autowired
    private IDriverService driverService;
    @Autowired
    public IRideRepository rideRepository;

    @Autowired
    public DriverController(IDriverService driverService) {
        this.driverService = driverService;
    }

    @PostMapping("/api/driver")
    public ResponseEntity<DriverDTO> createDriver(@RequestBody DriverDTO driverDTO) {
        Driver driver = driverDTO.to();
        driver = driverService.add(driver);
        return new ResponseEntity<>(DriverDTO.from(driver), HttpStatus.OK);
    }

    @GetMapping("/api/driver")
    public ResponseEntity<DriverDataPageDTO> getPaginatedDrivers(@PathParam("page") int page, @PathParam("size") int size) {
        DriverControllerMockProvider driverControllerMockProvider = new DriverControllerMockProvider();
        return new ResponseEntity<>(driverControllerMockProvider.getDriverDataPageDTO(), HttpStatus.OK);
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

    @PermitAll
    @GetMapping("/api/driver/{id}/ride")
    public ResponseEntity<ListDTO<Ride>> getRideHistory(@PathVariable(value = "id") Long id,
                                                     @PathParam("page") int page, @PathParam("size") int size,
                                                     @PathParam("from") String from, @PathParam("to") String to,
                                                     @PathParam("to") String sort) {
        Pageable pageParams =
                PageRequest.of(page, size, Sort.by(sort));

        DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
        LocalDateTime startTime = LocalDateTime.ofInstant(Instant.from(formatter.parse(from)), ZoneId.of(ZoneOffset.UTC.getId()));
        LocalDateTime endTime = LocalDateTime.ofInstant(Instant.from(formatter.parse(to)), ZoneId.of(ZoneOffset.UTC.getId()));
        Page<Ride> rides = rideRepository.getAllBetweenDates(startTime, endTime, id, pageParams);
        ListDTO<Ride> rideListDTO = new ListDTO<>();
        rideListDTO.setTotalCount(rides.getTotalElements());
        rideListDTO.setResults(rides.getContent());
        return new ResponseEntity<>(rideListDTO, HttpStatus.OK);

    }

}

