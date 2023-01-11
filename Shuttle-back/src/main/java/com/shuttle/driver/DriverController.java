package com.shuttle.driver;

import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
import com.shuttle.ride.IRideService;
import com.shuttle.ride.Ride;
import com.shuttle.ride.RideController;
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
	IRideService rideService;

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
            return new ResponseEntity<>("Driver does not exist!", HttpStatus.NOT_FOUND);
        }
        
		final GenericUser user____ = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		if (userService.isAdmin(user____)) {	
		} else {
	    	if (!driver.getId().equals(user____.getId())) {
                return new ResponseEntity<>("Driver does not exist!", HttpStatus.NOT_FOUND);
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
            return new ResponseEntity<>("Driver does not exist!", HttpStatus.NOT_FOUND);
        }
        
		final GenericUser user____ = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		if (userService.isAdmin(user____)) {	
		} else {
	    	if (!driver.getId().equals(user____.getId())) {
                return new ResponseEntity<>("Driver does not exist!", HttpStatus.NOT_FOUND);
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
            return new ResponseEntity<>("Driver does not exist!", HttpStatus.NOT_FOUND);
        }
        
		final GenericUser user____ = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		if (userService.isAdmin(user____)) {	
		} else {
	    	if (!driver.getId().equals(user____.getId())) {
                return new ResponseEntity<>("Driver does not exist!", HttpStatus.NOT_FOUND);
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
            return new ResponseEntity<>("Document does not exist!", HttpStatus.NOT_FOUND);
        }
        
		final GenericUser user____ = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		if (userService.isAdmin(user____)) {	
		} else {
	    	if (!dd.getDriver().getId().equals(user____.getId())) {
                return new ResponseEntity<>("Document does not exist!", HttpStatus.NOT_FOUND);
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
            return new ResponseEntity<>("Driver does not exist!", HttpStatus.NOT_FOUND);
        }
        
		final GenericUser user____ = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		if (userService.isAdmin(user____)) {	
		} else {
	    	if (!driver.getId().equals(user____.getId())) {
                return new ResponseEntity<>("Driver does not exist!", HttpStatus.NOT_FOUND);
	    	}
	    }
		
		// TODO: If file not image -> return 400 not an image
		// TODO: If file bigger than 5MB -> return 400 file bigger than 5mb
		
		DriverDocument doc = driverDocumentService.create(driver, driverDocumentDTO);
		return new ResponseEntity<>(new DriverDocumentDTO(doc), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('driver', 'admin', 'passenger')")
    @GetMapping("/api/driver/{id}/vehicle")
    public ResponseEntity<?> getVehicle(@PathVariable("id") Long id) {
    	if (id == null) {
    		return new ResponseEntity<>(new RESTError("Bad ID format!"), HttpStatus.BAD_REQUEST);
    	}

        Driver driver = driverService.get(id);
        if (driver == null) {
            return new ResponseEntity<>("Driver does not exist!", HttpStatus.NOT_FOUND);
        }

        Vehicle vehicle = vehicleService.findByDriver(driver);
        if (vehicle == null) {
            return new ResponseEntity<RESTError>(new RESTError("Vehicle is not assigned."), HttpStatus.BAD_REQUEST);
        }
        
        final GenericUser user____ = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        
		if (userService.isAdmin(user____)) {	
		} else if (userService.isDriver(user____)) {
	    	if (!driver.getId().equals(user____.getId())) {
                return new ResponseEntity<>("Driver does not exist!", HttpStatus.NOT_FOUND);
	    	}
	    } else if (userService.isPassenger(user____)) {
	    	// Always allow.
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

			MyValidator.validateLocation(vehicleDTO.getCurrentLocation(), "currentLocation");
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
            return new ResponseEntity<>("Driver does not exist!", HttpStatus.NOT_FOUND);
        }
        
        // If the driver already has a vehicle, remove it.
        
        Vehicle oldVehicle = vehicleService.findByDriver(driver);
        if (oldVehicle != null) {
        	vehicleService.removeDriver(oldVehicle);
        }

        vehicleDTO.setDriverId(driver.getId()); // It's kinda ugly to do it this way.
    	Vehicle v = vehicleService.add(vehicleDTO);
        return new ResponseEntity<>(VehicleDTO.from(v), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('admin', 'driver')")
    @PutMapping("/api/driver/{id}/vehicle")
    public ResponseEntity<?> changeVehicle(@RequestBody VehicleDTO vehicleDTO, @PathVariable(value = "id") Long id) {
    	try {
			MyValidator.validateRequired(vehicleDTO.getVehicleType(), "name");
			MyValidator.validateRequired(vehicleDTO.getModel(), "model");
			MyValidator.validateRequired(vehicleDTO.getLicenseNumber(), "licenseNumber");
			MyValidator.validateRequired(vehicleDTO.getPassengerSeats(), "passengerSeats");

			MyValidator.validateLocation(vehicleDTO.getCurrentLocation(), "currentLocation");
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
            return new ResponseEntity<>("Driver does not exist!", HttpStatus.NOT_FOUND);
        }
        
        final GenericUser user____ = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		if (userService.isAdmin(user____)) {	
		} else {
	    	if (!driver.getId().equals(user____.getId())) {
                return new ResponseEntity<>("Driver does not exist!", HttpStatus.NOT_FOUND);
	    	}
	    }
        
        Vehicle vehicle = vehicleService.findByDriver(driver);
        if (vehicle == null) {
        	// TODO: Maybe just create?
        	return new ResponseEntity<>("Vehicle does not exist!", HttpStatus.NOT_FOUND);
        }
        
        try {
        	vehicle = vehicleService.update(vehicle, vehicleDTO);
        } catch (IllegalArgumentException e) { // TODO: Specialize exception.
        	return new ResponseEntity<>("Vehicle type does not exist!", HttpStatus.NOT_FOUND);
        }
       
        return new ResponseEntity<>(VehicleDTO.from(vehicle), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('admin', 'driver')")
    @GetMapping("/api/driver/{id}/working-hour")
    public ResponseEntity<?> getWorkHoursHistory(@PathVariable Long id, Pageable pageable, @RequestParam(required = false) String from, @RequestParam(required = false) String to) {
    	if (id == null) {
			return new ResponseEntity<RESTError>(new RESTError("Field id is required!"), HttpStatus.BAD_REQUEST);
		}
    	
    	LocalDateTime tFrom = null, tTo = null;
    	if (from != null) {
    		try {
    			DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.of("UTC"));
    			tFrom = LocalDateTime.parse(from, formatter);
    		} catch (DateTimeParseException e) {
    			return new ResponseEntity<RESTError>(new RESTError("Field (from) format is not valid!"), HttpStatus.BAD_REQUEST);
    		}
    	}
    	if (to != null) {
    		try {
    			DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.of("UTC"));
    			tTo = LocalDateTime.parse(to, formatter);
    		} catch (DateTimeParseException e) {
    			return new ResponseEntity<RESTError>(new RESTError("Field (to) format is not valid!"), HttpStatus.BAD_REQUEST);
    		}
    	}

        final Driver driver = driverService.get(id);
        if (driver == null) {
            return new ResponseEntity<>("Driver does not exist!", HttpStatus.NOT_FOUND);
        }
        
        final GenericUser user____ = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		if (userService.isAdmin(user____)) {	
		} else {
	    	if (!driver.getId().equals(user____.getId())) {
                return new ResponseEntity<>("Driver does not exist!", HttpStatus.NOT_FOUND);
	    	}
	    }

        final ListDTO<WorkHoursNoDriverDTO> li = from(workHoursService.findAllByDriver(driver, pageable, tFrom, tTo));
        return new ResponseEntity<>(li, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('admin', 'driver')")
    @PostMapping("/api/driver/{id}/working-hour")
    public ResponseEntity<?> createWorkHours(@PathVariable Long id, @RequestBody WorkHoursNoDriverDTO dto) {
    	try {
			MyValidator.validateRequired(dto.getStart(), "start");
		} catch (MyValidatorException e1) {
			return new ResponseEntity<RESTError>(new RESTError(e1.getMessage()), HttpStatus.BAD_REQUEST);
		}
    	
    	LocalDateTime t = null;
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.of("UTC"));
			t = LocalDateTime.parse(dto.getStart(), formatter);
		} catch (DateTimeParseException e) {
			return new ResponseEntity<RESTError>(new RESTError("Field (dto) format is not valid!"), HttpStatus.BAD_REQUEST);
		}
		// assert(t != null);

    	if (id == null) {
    		return new ResponseEntity<>(new RESTError("Bad ID format!"), HttpStatus.BAD_REQUEST);
    	}
    	
    	Driver driver = driverService.get(id);
        if (driver == null) {
            return new ResponseEntity<>("Driver does not exist!", HttpStatus.NOT_FOUND);
        }
    	
        final GenericUser user____ = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		if (userService.isAdmin(user____)) {	
		} else {
	    	if (!driver.getId().equals(user____.getId())) {
                return new ResponseEntity<>("Driver does not exist!", HttpStatus.NOT_FOUND);
	    	}
	    }
		
		if (driverService.workedMoreThan8Hours(driver)) {
			return new ResponseEntity<>(new RESTError("Cannot start shift because you exceeded the 8 hours limit in last 24 hours!"), HttpStatus.BAD_REQUEST);
		}
		
		Vehicle vehicle = vehicleService.findByDriver(driver);
		if (vehicle == null) {
			return new ResponseEntity<>(new RESTError("Cannot start shift because the vehicle is not defined!"), HttpStatus.BAD_REQUEST);
		}
		
		final WorkHours lastWh = workHoursService.findLastByDriver(driver);
		if (lastWh != null) {
			if (lastWh.getFinish() == null) {
				return new ResponseEntity<>(new RESTError("Shift already ongoing!"), HttpStatus.BAD_REQUEST);
			}
		}
        
		WorkHours wh = workHoursService.addNew(driver, t);
        return new ResponseEntity<>(new WorkHoursNoDriverDTO(wh), HttpStatus.OK);

    }

    @PreAuthorize("hasAnyAuthority('admin', 'driver')")
    @GetMapping("/api/driver/working-hour/{working-hour-id}")
    public ResponseEntity<?> getWorkHours(@PathVariable("working-hour-id") Long id) {
    	if (id == null) {
    		return new ResponseEntity<>(new RESTError("Bad ID format!"), HttpStatus.BAD_REQUEST);
    	}
    	
    	WorkHours wh = workHoursService.findById(id);
        if (wh == null) {
            return new ResponseEntity<>("Working hour does not exist!", HttpStatus.NOT_FOUND);
        }
        
        final GenericUser user____ = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		if (userService.isAdmin(user____)) {	
		} else {
	    	if (!wh.getDriver().getId().equals(user____.getId())) {
                return new ResponseEntity<>("Working hour does not exist!", HttpStatus.NOT_FOUND);
	    	}
	    }
        
        return new ResponseEntity<>(new WorkHoursNoDriverDTO(wh), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('admin', 'driver')")
    @PutMapping("/api/driver/working-hour/{working-hour-id}")
    public ResponseEntity<?> changeWorkHours(@PathVariable("working-hour-id") Long id, @RequestBody WorkHoursNoDriverDTO dto) {
    	try {
			MyValidator.validateRequired(dto.getEnd(), "end");
		} catch (MyValidatorException e1) {
			return new ResponseEntity<RESTError>(new RESTError(e1.getMessage()), HttpStatus.BAD_REQUEST);
		}
    	
    	LocalDateTime t = null;
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.of("UTC"));
			t = LocalDateTime.parse(dto.getEnd(), formatter);
		} catch (DateTimeParseException e) {
			return new ResponseEntity<RESTError>(new RESTError("Field (dto) format is not valid!"), HttpStatus.BAD_REQUEST);
		}
		// assert(t != null);
		
    	if (id == null) {
    		return new ResponseEntity<>(new RESTError("Bad ID format!"), HttpStatus.BAD_REQUEST);
    	}
    	
    	WorkHours wh = workHoursService.findById(id);
        if (wh == null) {
            return new ResponseEntity<>("Working hour does not exist!", HttpStatus.NOT_FOUND);
        }
        	
		Driver driver = wh.getDriver();
		
        final GenericUser user____ = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		if (userService.isAdmin(user____)) {	
		} else {
	    	if (!driver.getId().equals(user____.getId())) {
                return new ResponseEntity<>("Working hour does not exist!", HttpStatus.NOT_FOUND);
	    	}
	    }

		Vehicle vehicle = vehicleService.findByDriver(driver);
		if (vehicle == null) {
			return new ResponseEntity<>(new RESTError("Cannot end shift because the vehicle is not defined!"), HttpStatus.BAD_REQUEST);
		}
		
		if (wh.getFinish() != null) {
			return new ResponseEntity<>(new RESTError("No shift is ongoing!"), HttpStatus.BAD_REQUEST);
		}
		
		wh = workHoursService.setEnd(wh, t);
        return new ResponseEntity<>(new WorkHoursNoDriverDTO(wh), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('driver', 'admin')")
    @GetMapping("/api/driver/{id}/ride")
    public ResponseEntity<?> getRideHistory(@PathVariable Long id, Pageable pageable, @RequestParam(required = false) String from, @RequestParam(required = false) String to) {
    	if (id == null) {
			return new ResponseEntity<RESTError>(new RESTError("Field id is required!"), HttpStatus.BAD_REQUEST);
		}
    	
    	LocalDateTime tFrom = null, tTo = null;
    	if (from != null && to != null) {
    		try {
    			DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.of("UTC"));
    			tFrom = LocalDateTime.parse(from, formatter);
    		} catch (DateTimeParseException e) {
    			return new ResponseEntity<RESTError>(new RESTError("Field (from) format is not valid!"), HttpStatus.BAD_REQUEST);
    		}
    		try {
    			DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.of("UTC"));
    			tTo = LocalDateTime.parse(to, formatter);
    		} catch (DateTimeParseException e) {
    			return new ResponseEntity<RESTError>(new RESTError("Field (to) format is not valid!"), HttpStatus.BAD_REQUEST);
    		}
    	}
    	
    	Driver d = driverService.get(id);	
		if (d == null) {
			return new ResponseEntity<>("Driver does not exist!", HttpStatus.NOT_FOUND);
		}

		final GenericUser user____ = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		if (userService.isAdmin(user____)) {	
		} else {
	    	if (!d.getId().equals(user____.getId())) {
                return new ResponseEntity<>("Driver does not exist!", HttpStatus.NOT_FOUND);
	    	}
	    }
		
		List<Ride> rides = rideService.findByUser(d, pageable, tFrom, tTo);
		ListDTO<RideDTO> ridesDTO = new ListDTO<>(rides.stream().map(r -> RideController.to(r)).toList());
        return new ResponseEntity<>(ridesDTO, HttpStatus.OK);
    }
}