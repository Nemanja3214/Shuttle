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
import com.shuttle.common.exception.NonExistantUserException;
import com.shuttle.driver.document.DriverDocument;
import com.shuttle.driver.document.DriverDocumentCreateDTO;
import com.shuttle.driver.document.DriverDocumentDTO;
import com.shuttle.driver.document.IDriverDocumentService;
import com.shuttle.driver.dto.DriverDTO;
import com.shuttle.driver.dto.DriverDataPageDTO;
import com.shuttle.driver.dto.DriverUpdateDTO;
import com.shuttle.passenger.Passenger;
import com.shuttle.passenger.PassengerDTO;
import com.shuttle.ride.IRideRepository;
import com.shuttle.ride.Ride;
import com.shuttle.ride.dto.RideDTO;
import com.shuttle.user.GenericUser;
import com.shuttle.user.UserService;
import com.shuttle.user.dto.UserDTONoPassword;
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
import org.springframework.security.core.context.SecurityContextHolder;
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
    private IDriverDocumentService driverDocumentService;

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
		UserDTONoPassword result = new UserDTONoPassword(d);
		return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('admin')")
    @GetMapping("/api/driver")
    public ResponseEntity<?> getPaginatedDrivers(Pageable pageable) {
    	List<Driver> drivers = this.driverService.findAll(pageable);
		List<UserDTONoPassword> passengersDTO = drivers.stream().map(d -> new UserDTONoPassword(d)).toList();
		ListDTO<UserDTONoPassword> result = new ListDTO<>(passengersDTO);
		return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('driver', 'admin')")
    @GetMapping("/api/driver/{id}")
    public ResponseEntity<?> getDriverDetails(@PathVariable("id") Long id) {
    	if (id == null) {
            return new ResponseEntity<RESTError>(new RESTError("Field (id) is required!"), HttpStatus.BAD_REQUEST);
        }

        Driver driver = driverService.get(id);
        if (driver == null) {
            return new ResponseEntity<>(new RESTError("Driver does not exist!"), HttpStatus.NOT_FOUND);
        }
        
		final GenericUser user____ = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		if (userService.isAdmin(user____)) {	
		} else {
	    	if (!driver.getId().equals(user____.getId())) {
                return new ResponseEntity<RESTError>(new RESTError("Driver does not exist!"), HttpStatus.NOT_FOUND);
	    	}
	    }
		
        return new ResponseEntity<>(new UserDTONoPassword(driver), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('driver', 'admin')")
    @PutMapping("/api/driver/{id}")
    public ResponseEntity<?> updateDriver(@RequestBody DriverUpdateDTO dto, @PathVariable("id") Long id) {
    	try {
			MyValidator.validateRequired(dto.getName(), "name");
			MyValidator.validateRequired(dto.getSurname(), "surname");
			MyValidator.validateRequired(dto.getEmail(), "email");
			MyValidator.validateRequired(dto.getAddress(), "address");
			
			MyValidator.validateLength(dto.getName(), "name", 100);
			MyValidator.validateLength(dto.getSurname(), "surname", 100);
			MyValidator.validateLength(dto.getTelephoneNumber(), "telephoneNumber", 18);
			MyValidator.validateLength(dto.getEmail(), "email", 100);
			MyValidator.validateLength(dto.getAddress(), "address", 100);
		} catch (MyValidatorException e1) {
			return new ResponseEntity<RESTError>(new RESTError(e1.getMessage()), HttpStatus.BAD_REQUEST);
		}
    	
    	if (id == null) {
    		return new ResponseEntity<>(new RESTError("Bad ID format!"), HttpStatus.BAD_REQUEST);
    	}
    	
    	Driver driver = driverService.get(id);
        if (driver == null) {
            return new ResponseEntity<>(new RESTError("Driver does not exist!"), HttpStatus.NOT_FOUND);
        }
        
		final GenericUser user____ = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		if (userService.isAdmin(user____)) {	
		} else {
	    	if (!driver.getId().equals(user____.getId())) {
                return new ResponseEntity<RESTError>(new RESTError("Driver does not exist!"), HttpStatus.NOT_FOUND);
	    	}
	    }
		
		try {
			driver = this.driverService.update(driver, dto);
		} catch (IOException e) {
			return new ResponseEntity<>("Cannot save picture", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(new UserDTONoPassword(driver), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('driver', 'admin')")
    @GetMapping("/api/driver/{id}/documents")
    public ResponseEntity<?> getDriverDocs(@PathVariable("id") Long id) {
    	if (id == null) {
    		return new ResponseEntity<>(new RESTError("Bad ID format!"), HttpStatus.BAD_REQUEST);
    	}
    	
    	Driver driver = driverService.get(id);
        if (driver == null) {
            return new ResponseEntity<>(new RESTError("Driver does not exist!"), HttpStatus.NOT_FOUND);
        }
        
		final GenericUser user____ = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		if (userService.isAdmin(user____)) {	
		} else {
	    	if (!driver.getId().equals(user____.getId())) {
                return new ResponseEntity<RESTError>(new RESTError("Driver does not exist!"), HttpStatus.NOT_FOUND);
	    	}
	    }
		
		List<DriverDocument> dd = driverDocumentService.findByDriver(driver);
		List<DriverDocumentDTO> ddDto = dd.stream().map(doc -> new DriverDocumentDTO(doc)).toList();
		return new ResponseEntity<>(ddDto, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('driver', 'admin')")
    @DeleteMapping("/api/driver/document/{document-id}")
    public ResponseEntity<?> deleteDocsById(@PathVariable("document-id") Long id) {
    	if (id == null) {
    		return new ResponseEntity<>(new RESTError("Bad ID format!"), HttpStatus.BAD_REQUEST);
    	}
    	
    	DriverDocument dd = driverDocumentService.findById(id);
        if (dd == null) {
            return new ResponseEntity<>(new RESTError("Document does not exist!"), HttpStatus.NOT_FOUND);
        }
        
		final GenericUser user____ = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		if (userService.isAdmin(user____)) {	
		} else {
	    	if (!dd.getDriver().getId().equals(user____.getId())) {
                return new ResponseEntity<RESTError>(new RESTError("Document does not exist!"), HttpStatus.NOT_FOUND);
	    	}
	    }
		
		driverDocumentService.delete(dd);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAnyAuthority('driver', 'admin')")
    @PostMapping("/api/driver/{id}/documents")
    public ResponseEntity<?> addDocsById(@PathVariable("id") Long id, @RequestBody DriverDocumentCreateDTO driverDocumentDTO) {
    	try {
			MyValidator.validateRequired(driverDocumentDTO.getName(), "name");
			MyValidator.validateRequired(driverDocumentDTO.getDocumentImage(), "documentImage");
			
			MyValidator.validateLength(driverDocumentDTO.getName(), "name", 100);
		} catch (MyValidatorException e1) {
			return new ResponseEntity<RESTError>(new RESTError(e1.getMessage()), HttpStatus.BAD_REQUEST);
		}
    	
    	if (id == null) {
    		return new ResponseEntity<>(new RESTError("Bad ID format!"), HttpStatus.BAD_REQUEST);
    	}
    	
    	Driver driver = driverService.get(id);
        if (driver == null) {
            return new ResponseEntity<>(new RESTError("Driver does not exist!"), HttpStatus.NOT_FOUND);
        }
        
		final GenericUser user____ = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		if (userService.isAdmin(user____)) {	
		} else {
	    	if (!driver.getId().equals(user____.getId())) {
                return new ResponseEntity<RESTError>(new RESTError("Driver does not exist!"), HttpStatus.NOT_FOUND);
	    	}
	    }
		
		// TODO: If file not image -> return 400 not an image
		// TODO: If file bigger than 5MB -> return 400 file bigger than 5mb
		
		DriverDocument doc = driverDocumentService.create(driver, driverDocumentDTO);
		return new ResponseEntity<>(new DriverDocumentDTO(doc), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('driver', 'admin')")
    @GetMapping("/api/driver/{id}/vehicle")
    public ResponseEntity<?> getVehicle(@PathVariable("id") Long id) {
    	if (id == null) {
    		return new ResponseEntity<>(new RESTError("Bad ID format!"), HttpStatus.BAD_REQUEST);
    	}

        Driver driver = driverService.get(id);
        if (driver == null) {
            return new ResponseEntity<>(new RESTError("Driver does not exist!"), HttpStatus.NOT_FOUND);
        }

        Vehicle vehicle = vehicleService.findByDriver(driver);
        if (vehicle == null) {
            return new ResponseEntity<RESTError>(new RESTError("Vehicle is not assigned."), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(VehicleDTO.from(vehicle), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('admin')")
    @PostMapping("/api/driver/{id}/vehicle")
    public ResponseEntity<?> createVehicle(@RequestBody VehicleDTO vehicleDTO, @PathVariable("id") Long id) {
    	try {
			MyValidator.validateRequired(vehicleDTO.getVehicleType(), "name");
			MyValidator.validateRequired(vehicleDTO.getModel(), "model");
			MyValidator.validateRequired(vehicleDTO.getLicenseNumber(), "licenseNumber");
			MyValidator.validateRequired(vehicleDTO.getPassengerSeats(), "passengerSeats");

			MyValidator.validateLength(vehicleDTO.getModel(), "name", 100);
			MyValidator.validateLength(vehicleDTO.getLicenseNumber(), "licenseNumber", 20);
			MyValidator.validateRange(vehicleDTO.getPassengerSeats().longValue(), "passengerSeats", 1L, 20L);
		} catch (MyValidatorException e1) {
			return new ResponseEntity<RESTError>(new RESTError(e1.getMessage()), HttpStatus.BAD_REQUEST);
		}
    	
    	if (id == null) {
    		return new ResponseEntity<>(new RESTError("Bad ID format!"), HttpStatus.BAD_REQUEST);
    	}

        Driver driver = driverService.get(id);
        if (driver == null) {
            return new ResponseEntity<>(new RESTError("Driver does not exist!"), HttpStatus.NOT_FOUND);
        }

        vehicleDTO.setDriverId(driver.getId()); // It's kinda ugly to do it this way.
        
    	Vehicle v = vehicleService.add(vehicleDTO);
        return new ResponseEntity<>(VehicleDTO.from(v), HttpStatus.OK);
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

