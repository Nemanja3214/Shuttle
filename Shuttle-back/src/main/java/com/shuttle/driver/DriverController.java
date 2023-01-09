package com.shuttle.driver;

import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.shuttle.common.ListDTO;
import com.shuttle.common.RESTError;
import com.shuttle.driver.dto.DriverDTO;
import com.shuttle.driver.dto.DriverDataPageDTO;
import com.shuttle.driver.dto.DriverDocumentDTO;
import com.shuttle.passenger.Passenger;
import com.shuttle.passenger.PassengerDTO;
import com.shuttle.ride.IRideRepository;
import com.shuttle.ride.Ride;
import com.shuttle.ride.dto.RideDTO;
import com.shuttle.user.GenericUser;
import com.shuttle.user.UserService;
import com.shuttle.util.MyValidator;
import com.shuttle.util.MyValidatorException;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.data.domain.Page;

import com.shuttle.vehicle.IVehicleService;
import com.shuttle.vehicle.Vehicle;
import com.shuttle.vehicle.VehicleDTO;
import com.shuttle.workhours.WorkHours;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.server.PathParam;
import com.shuttle.workhours.*;
import com.shuttle.workhours.dto.WorkHoursNoDriverDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.Writer;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
public class DriverController {
    @Autowired
    private IDriverService driverService;
    @Autowired
    private IRideRepository rideRepository;
    @Autowired
    private IVehicleService vehicleService;
    @Autowired
    private UserService userService;

	@Autowired
	private IWorkHoursService workHoursService;

    private ListDTO<WorkHoursNoDriverDTO> from(List<WorkHours> workHours) {
        return new ListDTO<>(workHours.stream().map(w -> new WorkHoursNoDriverDTO(w)).toList());
    }

    @PreAuthorize("hasAnyAuthority('admin')")
    @PostMapping("/api/driver")
    public ResponseEntity<?> createDriver(@RequestBody DriverDTO dto) {
    	try {
			MyValidator.validateRequired(dto.getName(), "name");
			MyValidator.validateRequired(dto.getSurname(), "surname");
			MyValidator.validateRequired(dto.getEmail(), "email");
			MyValidator.validateRequired(dto.getAddress(), "address");
			MyValidator.validateRequired(dto.getPassword(), "password");
			
			MyValidator.validateLength(dto.getName(), "name", 100);
			MyValidator.validateLength(dto.getSurname(), "surname", 100);
			MyValidator.validateLength(dto.getTelephoneNumber(), "telephoneNumber", 18);
			MyValidator.validateLength(dto.getEmail(), "email", 100);
			MyValidator.validateLength(dto.getAddress(), "address", 100);
			
			MyValidator.validatePattern(dto.getPassword(), "password", "^(?=.*\\d)(?=.*[A-Z])(?!.*[^a-zA-Z0-9@#$^+=])(.{8,15})$");
		} catch (MyValidatorException e1) {
			return new ResponseEntity<RESTError>(new RESTError(e1.getMessage()), HttpStatus.BAD_REQUEST);
		}
		
		GenericUser userWithThisEmail = userService.findByEmail(dto.getEmail());
		if (userWithThisEmail != null) {
			return new ResponseEntity<RESTError>(new RESTError("User with that email already exists!"), HttpStatus.BAD_REQUEST);
		}
		
		Driver d = driverService.create(dto);
		DriverDTO result = DriverDTO.from(d);
		return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('admin')")
    @GetMapping("/api/driver")
    public ResponseEntity<?> getPaginatedDrivers(Pageable pageable) {
    	List<Driver> drivers = this.driverService.findAll(pageable);
		List<DriverDTO> passengersDTO = drivers.stream().map(d -> DriverDTO.from(d)).toList();
		ListDTO<DriverDTO> result = new ListDTO<>(passengersDTO);
		return new ResponseEntity<>(result, HttpStatus.OK);
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
    public ResponseEntity<?> getVehicle(@PathVariable(value = "id") Long id) {
        if (id == null) {
            return new ResponseEntity<Void>((Void)null, HttpStatus.BAD_REQUEST);
        }

        final Driver driver = driverService.get(id);

        if (driver == null) {
            return new ResponseEntity<Void>((Void)null, HttpStatus.NOT_FOUND);
        }

        Vehicle vehicle = vehicleService.findByDriver(driver);
        if (vehicle == null) {
            return new ResponseEntity<RESTError>(new RESTError("Vehicle is not assigned."), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(VehicleDTO.from(vehicle), HttpStatus.OK);
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
    public ResponseEntity<?> getWorkHoursHistory(@PathVariable(value = "id") Long id, Pageable pageable, @RequestParam("from") String from, @RequestParam("to") String to) {
        if (id == null) {
            return new ResponseEntity<Void>((Void)null, HttpStatus.BAD_REQUEST);
        }

        final Driver driver = driverService.get(id);

        if (driver == null) {
            return new ResponseEntity<Void>((Void)null, HttpStatus.NOT_FOUND);
        }

        LocalDateTime fromDate, toDate;

        try {
            fromDate = LocalDateTime.parse(from);
            toDate = LocalDateTime.parse(to);
        } catch (DateTimeParseException ex) {
            return new ResponseEntity<Void>((Void)null, HttpStatus.BAD_REQUEST);
        }

        final ListDTO<WorkHoursNoDriverDTO> li = from(workHoursService.findAllByDriver(driver, pageable, fromDate, toDate));
        return new ResponseEntity<>(li, HttpStatus.OK);
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

    @PreAuthorize("hasAnyAuthority('driver', 'admin')")
    @GetMapping("/api/driver/{id}/ride")
    public ResponseEntity<ListDTO<Ride>> getRideHistory(@PathVariable(value = "id") Long id,
                                                           @PathParam("page") int page, @PathParam("size") int size,
                                                           @PathParam("from") String from, @PathParam("to") String to,
                                                           @PathParam("sort") String sort) {
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

