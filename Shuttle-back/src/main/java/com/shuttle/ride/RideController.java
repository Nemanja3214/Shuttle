package com.shuttle.ride;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shuttle.common.RESTError;
import com.shuttle.common.exception.FavoriteRideLimitExceeded;
import com.shuttle.common.exception.NonExistantFavoriteRoute;
import com.shuttle.common.exception.NonExistantUserException;
import com.shuttle.common.exception.NonExistantVehicleType;
import com.shuttle.driver.Driver;
import com.shuttle.driver.IDriverService;
import com.shuttle.location.FavoriteRoute;
import com.shuttle.location.ILocationService;
import com.shuttle.location.Location;
import com.shuttle.location.Route;
import com.shuttle.location.dto.FavoriteRouteDTO;
import com.shuttle.location.dto.LocationDTO;
import com.shuttle.location.dto.RouteDTO;
import com.shuttle.panic.IPanicService;
import com.shuttle.panic.Panic;
import com.shuttle.panic.PanicDTO;
import com.shuttle.panic.PanicSendDTO;
import com.shuttle.passenger.IPassengerRepository;
import com.shuttle.passenger.IPassengerService;
import com.shuttle.passenger.Passenger;
import com.shuttle.ride.Ride.Status;
import com.shuttle.ride.cancellation.Cancellation;
import com.shuttle.ride.cancellation.ICancellationService;
import com.shuttle.ride.cancellation.dto.CancellationBodyDTO;
import com.shuttle.ride.cancellation.dto.CancellationDTO;
import com.shuttle.ride.dto.CreateRideDTO;
import com.shuttle.ride.dto.GraphEntryDTO;
import com.shuttle.ride.dto.RideDTO;
import com.shuttle.ride.dto.RideDriverDTO;
import com.shuttle.ride.dto.RidePassengerDTO;
import com.shuttle.user.GenericUser;
import com.shuttle.user.UserService;
import com.shuttle.user.dto.UserDTONoPassword;
import com.shuttle.util.MyValidator;
import com.shuttle.util.MyValidatorException;
import com.shuttle.vehicle.vehicleType.IVehicleTypeRepository;
import com.shuttle.vehicle.vehicleType.VehicleType;

@RestController
@RequestMapping("/api/ride")
public class RideController {
    @Autowired
    private IRideService rideService;
    @Autowired
    private IDriverService driverService;
    @Autowired
    private IVehicleTypeRepository vehicleTypeRepository;
    @Autowired
    private IPassengerRepository passengerRepository;
    @Autowired
    private ILocationService locationService;
    @Autowired
    private ICancellationService cancellationService;
    @Autowired
    private SimpMessagingTemplate template;
    @Autowired
    private IPassengerService passengerService;
    @Autowired
    private IPanicService panicService;
    @Autowired
    private UserService userService;

    /**
     * DTO Mapper function.
     * @param rideDTO DTO object.
     * @param driver Driver. Can be null (e.g. if the ride is scheduled in the future and no
     * driver is available now).
     * @return Ride object made from the DTO.
     */
    private Ride from(CreateRideDTO rideDTO, Driver driver) {
        final Double velocity = 60.0 / 1000.0; // TODO: Where to get average vehicle velocity from?

        final VehicleType vehicleType = vehicleTypeRepository
                .findVehicleTypeByNameIgnoreCase(rideDTO.getVehicleType())
                .orElseThrow();
        final Double cost = (vehicleType.getPricePerKM() + 120) * Math.round(rideDTO.getDistance() / 1000.0);

        final List<Passenger> passengers = rideDTO.getPassengers()
                .stream()
                .map(userInfo -> passengerService.findByEmail(userInfo.getEmail()))
                .collect(Collectors.toList());

        final List<RouteDTO> routeDTO = rideDTO.getLocations();

        // .stream().toList() returns an *immutable* list, hence the: new ArrayList<...>().

        final List<LocationDTO> locationsDTO = new ArrayList<LocationDTO>(
                routeDTO.stream().map(rou -> rou.getDeparture()).toList());
        locationsDTO.add(routeDTO.get(routeDTO.size() - 1).getDestination());
        final List<Location> locations = locationsDTO.stream().map(loc -> locationService.findOrAdd(loc)).toList();

        Route route = new Route();
        route.setLocations(locations);

        Ride r = new Ride();
        r.setStatus(Status.PENDING);
        r.setTotalCost(cost);
        r.setDriver(driver);
        r.setVehicleType(vehicleType);
        r.setBabyTransport(rideDTO.getBabyTransport());
        r.setPetTransport(rideDTO.getPetTransport());
        r.setEstimatedTimeInMinutes((int) ((Math.round(rideDTO.getDistance() / 1000.0) / velocity) * 60));
        r.setPassengers(passengers);
        r.setRoute(route);
             
        LocalDateTime scheduledTime = null;
        if (rideDTO.getScheduledTime() != null) {
        	scheduledTime = LocalDateTime.parse(rideDTO.getScheduledTime(), DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.of("UTC")));
        }
        r.setScheduledTime(scheduledTime);

        return r;
    }

    /**
     * DTO Mapper function
     * @param ride Model object.
     * @return DTO made from the ride.
     */
    public static RideDTO to(Ride ride) {
        RideDTO rideDTO = new RideDTO();
        rideDTO.setId(ride.getId());

        if (ride.getStartTime() != null) {
            rideDTO.setStartTime(ride.getStartTime().format(DateTimeFormatter.ISO_DATE_TIME));
        }

        if (ride.getEndTime() != null) {
            rideDTO.setEndTime(ride.getEndTime().format(DateTimeFormatter.ISO_DATE_TIME));
        }

        rideDTO.setTotalCost(ride.getTotalCost());
        rideDTO.setDriver(new RideDriverDTO(ride.getDriver()));
        rideDTO.setPassengers(ride.getPassengers().stream().map(p -> new RidePassengerDTO(p)).toList());
        rideDTO.setEstimatedTimeInMinutes(ride.getEstimatedTimeInMinutes());
        rideDTO.setBabyTransport(ride.getBabyTransport());
        rideDTO.setPetTransport(ride.getPetTransport());
        rideDTO.setVehicleType(ride.getVehicleType().getName());
        rideDTO.setScheduledTime(ride.getScheduledTime() == null ? null : ride.getScheduledTime().toString());
        
        if (ride.getRejection() != null) {
            rideDTO.setRejection(new CancellationDTO(ride.getRejection()));
        }

        rideDTO.setStatus(ride.getStatus());

        List<RouteDTO> locationsDTO = new ArrayList<>();
        List<Location> ls = ride.getLocations();
        for (int i = 0; i < ls.size(); i += 2) {
            LocationDTO from = LocationDTO.from(ls.get(i));
            LocationDTO to = LocationDTO.from(ls.get(i + 1));

            RouteDTO d = new RouteDTO(from, to);
            locationsDTO.add(d);
        }
        rideDTO.setLocations(locationsDTO);

        return rideDTO;
    }

    /**
     * Send a Ride object to the driver with the given driverId.
     * If invalid data, nothing is sent.
     * If driver has no rides, nothing is sent.
     */
    @MessageMapping("/ride/driver/{driverId}")
    public void driverFetchRide(@DestinationVariable Long driverId) {
        if (driverId == null) {
            return;
        }

        final Driver driver = driverService.get(driverId);
        if (driver == null) {
            return;
        }

        // Ride has no driver, ride finds driver -> notify passengers.
        boolean shouldNotifyPassengersOfThisRideForAnyChanges = false;


        Ride ride = rideService.findCurrentRideByDriver(driver);
        if (ride == null) {
            // Try to find a ride scheduled in the future that isn't assigned to anybody.
            // If multiple, give priority to the one that's furthest away chronologically.
            final Optional<Ride> r = rideService.findRidesWithNoDriver()
                .stream()
                .sorted((r1, r2) -> {
                    // Descending order, so r2.compare(r1).
                    return r2.getStartTime().compareTo(r1.getStartTime());
                })
                .findFirst();

            if (r.isPresent()) {
                ride = r.get();

                // Check if this driver has the appropriate vehicle for this ride.
                if (rideService.requestParamsMatch(driver, ride.getBabyTransport(), ride.getPetTransport(), ride.getPassengers().size(), ride.getVehicleType())) {
                    ride.setDriver(driver);
                    rideService.save(ride);
                    shouldNotifyPassengersOfThisRideForAnyChanges = true;
                } else {
                    ride = null;
                }
            } else {
                ride = null;
            }   
        }

        final String dest = String.format("/ride/driver/%d", driverId.longValue());

        if (ride != null) {
            if (ride.getDriver().isAvailable()) {
                driverService.setAvailable(ride.getDriver(), false);
            }
            template.convertAndSend(dest, to(ride));

            if (shouldNotifyPassengersOfThisRideForAnyChanges) {
                notifyRidePassengers(ride);
            }
        }
    }

    @Scheduled(fixedDelay = 300000) // 300000 == 300 * 1000ms = (5 * 60) * 10000ms
    public void notifyPassengerAboutFutureRide() {
        for (Ride r : rideService.findAllPendingInFuture()) {
            notifyRidePassengers(r);
        }
    }

    @MessageMapping("/ride/passenger/{passengerId}")
    public void passengerFetchRide(@DestinationVariable Long passengerId) {
        if (passengerId == null) {
            return;
        }

        final Passenger passenger = passengerService.findById(passengerId);
        if (passenger == null) {
            return;
        }

        final Ride ride = rideService.findCurrentRideByPassenger(passenger);
        final String dest = String.format("/ride/passenger/%d", passengerId.longValue());
        if (ride == null) {
            return;
            //template.convertAndSend(dest, (Void) null);
        }

        template.convertAndSend(dest, to(ride));
    }

    public void notifyRideDriver(Ride ride) {
        // Pre-condition: ride is in the database

        if (ride.getDriver() == null) {
            return;
        }

        Long driverId = ride.getDriver().getId();
        final String dest = String.format("/ride/driver/%d", driverId.longValue());
        template.convertAndSend(dest, to(ride));
    }

    public void notifyRidePassengers(Ride ride) {
        // Pre-condition: ride is in the database
        // Pre-condition: ride.passengers; each passenger is in the database

        for (Passenger p : ride.getPassengers()) {
            Long passengerId = p.getId();
            final String dest = String.format("/ride/passenger/%d", passengerId.longValue());
            template.convertAndSend(dest, to(ride));
        }
    }

    @PreAuthorize("hasAnyAuthority('passenger')")
    @PostMapping
    public ResponseEntity<?> createRide(@RequestBody CreateRideDTO createRideDTO) {
    	LocalDateTime scheduledFor; 
    	try {
			MyValidator.validateRequired(createRideDTO.getLocations(), "locations");
			MyValidator.validateRequired(createRideDTO.getPassengers(), "passengers");
			MyValidator.validateRequired(createRideDTO.getVehicleType(), "vehicleType");
			MyValidator.validateRequired(createRideDTO.getBabyTransport(), "babyTransport");
			MyValidator.validateRequired(createRideDTO.getBabyTransport(), "petTransport");
			MyValidator.validateRequired(createRideDTO.getDistance(), "distance");
			
			MyValidator.validateUserRef(createRideDTO.getPassengers(), "passengers");
			MyValidator.validateRouteDTO(createRideDTO.getLocations(), "locations");
			
			scheduledFor = MyValidator.validateDateTime(createRideDTO.getScheduledTime(), "scheduledTime");
			
			MyValidator.validateLength(createRideDTO.getVehicleType(), "vehicleType", 50);
		} catch (MyValidatorException e1) {
			return new ResponseEntity<RESTError>(new RESTError(e1.getMessage()), HttpStatus.BAD_REQUEST);
		}	
    	
    	System.out.println("AAA " + createRideDTO.toString());
    	try {
            final Passenger p = (Passenger)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());

            if (rideService.findCurrentRideByPassenger(p) != null) {
                return new ResponseEntity<RESTError>(new RESTError("Cannot create a ride while you have one already pending!"), HttpStatus.BAD_REQUEST);
            }

            final boolean forFuture = scheduledFor != null;
            final Driver driver = rideService.findMostSuitableDriver(createRideDTO, forFuture);
            final Ride ride = from(createRideDTO, driver);

            ride.setScheduledTime(scheduledFor);
            ride.setTotalLength(createRideDTO.getLocations().stream().mapToDouble(route -> route.getDistance()).sum());
            rideService.save(ride);
            notifyRidePassengers(ride);

            if (driver != null) {
                driverService.setAvailable(driver, false);
                notifyRideDriver(ride);
            }

            return new ResponseEntity<RideDTO>(to(ride), HttpStatus.OK);
        } catch (NoAvailableDriverException e) {
            return new ResponseEntity<RESTError>(new RESTError("No driver available!"), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAnyAuthority('admin', 'driver')")
    @GetMapping("/driver/{driverId}/active")
    public ResponseEntity<?> getActiveRideByDriver(@PathVariable Long driverId) {
    	if (driverId == null) {
            return new ResponseEntity<RESTError>(new RESTError("Bad ID format."), HttpStatus.BAD_REQUEST);
        }
    	
        final GenericUser user = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        if (userService.isAdmin(user)) {
	    } else if (userService.isDriver(user)) {
            if (user.getId() != driverId) {
                return new ResponseEntity<>("Active ride does not exist!", HttpStatus.NOT_FOUND);
            }
        }
        
        final Driver driver = driverService.get(driverId);

		if (driver == null) {
		    return new ResponseEntity<>("Active ride does not exist!", HttpStatus.NOT_FOUND);
		} 
  
		Ride ride = rideService.findCurrentRideByDriver(driver);
		if (ride == null) {
			return new ResponseEntity<>("Active ride does not exist!", HttpStatus.NOT_FOUND);
		}
       
       return new ResponseEntity<>(to(ride), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('admin', 'passenger')")
    @GetMapping("/passenger/{passengerId}/active")
    public ResponseEntity<?> getActiveRideByPassenger(@PathVariable Long passengerId) {
        if (passengerId == null) {
            return new ResponseEntity<RESTError>(new RESTError("Bad ID format."), HttpStatus.BAD_REQUEST);
        }
        
        final GenericUser user = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        if (userService.isAdmin(user)) {
	    } else if (userService.isPassenger(user)) {
            if (user.getId() != passengerId) {
                return new ResponseEntity<>("Active ride does not exist!", HttpStatus.NOT_FOUND);
            }
        }

        final Passenger passenger = passengerService.findById(passengerId);
        if (passenger == null) {
            return new ResponseEntity<>("Active ride does not exist!", HttpStatus.NOT_FOUND);
        }


        Ride ride = rideService.findCurrentRideByPassenger(passenger);
        if (ride == null) {
            return new ResponseEntity<>("Active ride does not exist!", HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<>(to(ride), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('admin', 'passenger', 'driver')")
    @GetMapping("/{rideId}")
    public ResponseEntity<?> getRide(@PathVariable Long rideId) {
    	if (rideId == null) {
            return new ResponseEntity<RESTError>(new RESTError("Bad ID format."), HttpStatus.BAD_REQUEST);
        }

        final Ride ride = rideService.findById(rideId);
        if (ride == null) {
            return new ResponseEntity<>("Ride does not exist!", HttpStatus.NOT_FOUND);
        }
        
        final GenericUser user = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        if (userService.isAdmin(user)) {	
	    } else if (userService.isPassenger(user)) {
	    	if (ride.getPassengers().stream().noneMatch(p -> p.getId().equals(user.getId()))) {
                return new ResponseEntity<>("Ride does not exist!", HttpStatus.NOT_FOUND);
            }	
	    } else if (userService.isDriver(user)) {
            if (!ride.getDriver().getId().equals(user.getId())) {
            	return new ResponseEntity<>("Ride does not exist!", HttpStatus.NOT_FOUND);
            }
        }
       
        return new ResponseEntity<RideDTO>(to(ride), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('passenger')")
    @PutMapping("/{id}/withdraw")
    public ResponseEntity<?> withdrawRide(@PathVariable Long id) {
        if (id == null) {
            return new ResponseEntity<RESTError>(new RESTError("Bad ID format."), HttpStatus.BAD_REQUEST);
        }

        Ride ride = rideService.findById(id);
        if (ride == null) {
            return new ResponseEntity<>("Ride does not exist!", HttpStatus.NOT_FOUND);
        }
        
        final GenericUser user = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        if (userService.isPassenger(user)) {
	    	if (ride.getPassengers().stream().noneMatch(p -> p.getId().equals(user.getId()))) {
                return new ResponseEntity<>("Ride does not exist!", HttpStatus.NOT_FOUND);
            }	
	    }

        if (ride.getStatus() != Status.PENDING && ride.getStatus() != Status.STARTED) {
            return new ResponseEntity<RESTError>(new RESTError("Cannot cancel a ride that isn't PENDING or STARTED."), HttpStatus.BAD_REQUEST);
        }

        this.rideService.cancelRide(ride);

        // ride.getDriver() can be null if the ride is scheduled in the future, before a driver has been assigned.
        if (ride.getDriver() != null) {
            driverService.setAvailable(ride.getDriver(), true);
            notifyRideDriver(ride);
        }

        notifyRidePassengers(ride);

        return new ResponseEntity<RideDTO>(to(ride), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('passenger', 'driver')")
    @PutMapping("/{id}/panic")
    public ResponseEntity<?> panicRide(@PathVariable Long id, @RequestBody PanicSendDTO reason) {   
    	try {
			MyValidator.validateRequired(reason.getReason(), "reason");
		} catch (MyValidatorException e1) {
			return new ResponseEntity<RESTError>(new RESTError(e1.getMessage()), HttpStatus.BAD_REQUEST);
		}
    	
    	if (id == null) {
            return new ResponseEntity<Void>((Void)null, HttpStatus.BAD_REQUEST);
        }
        
        Ride ride = rideService.findById(id);   
        if (ride == null) {
            return new ResponseEntity<>("Ride does not exist!", HttpStatus.NOT_FOUND);
        }
        
        final GenericUser user = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        if (userService.isPassenger(user)) {
	    	if (ride.getPassengers().stream().noneMatch(p -> p.getId().equals(user.getId()))) {
                return new ResponseEntity<>("Ride does not exist!", HttpStatus.NOT_FOUND);
            }
	    } else if (userService.isDriver(user)) {
            if (!ride.getDriver().getId().equals(user.getId())) {
            	return new ResponseEntity<>("Ride does not exist!", HttpStatus.NOT_FOUND);
            }
        }
        
        rideService.cancelRide(ride);
        driverService.setAvailable(ride.getDriver(), true);
        
        Panic p = panicService.add(ride, user, reason.getReason());
        PanicDTO dto = new PanicDTO();
        dto.setReason(p.getReason());
        dto.setTime(p.getTime().toString());
        dto.setId(p.getId());
        dto.setRide(to(ride));
        dto.setUser(new UserDTONoPassword(p.getUser()));
        
        notifyRideDriver(ride);
        notifyRidePassengers(ride);

        return new ResponseEntity<PanicDTO>(dto, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('driver')")
    @PutMapping("/{id}/start")
    public ResponseEntity<?> startRide(@PathVariable Long id) {
    	if (id == null) {
            return new ResponseEntity<RESTError>(new RESTError("Bad ID format."), HttpStatus.BAD_REQUEST);
        }

        Ride ride = rideService.findById(id);
        if (ride == null) {
        	return new ResponseEntity<>("Ride does not exist!", HttpStatus.NOT_FOUND);
        }

        final GenericUser user = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        if (userService.isDriver(user)) {
            if (!ride.getDriver().getId().equals(user.getId())) {
            	return new ResponseEntity<>("Ride does not exist!", HttpStatus.NOT_FOUND);
            }
        }
        
        if (ride.getStatus() != Ride.Status.ACCEPTED) {
        	return new ResponseEntity<RESTError>(new RESTError("Cannot start a ride that is not in status ACCEPTED!"), HttpStatus.BAD_REQUEST);
        }
        
        rideService.startRide(ride);
        driverService.setAvailable(ride.getDriver(), false);
        notifyRidePassengers(ride);
        notifyRideDriver(ride);
        
        RideDTO dto = to(ride);
        return new ResponseEntity<RideDTO>(dto, HttpStatus.OK);
    }
    
    @PreAuthorize("hasAnyAuthority('driver')")
    @PutMapping("/{id}/accept")
    public ResponseEntity<?> acceptRide(@PathVariable Long id) {
        if (id == null) {
            return new ResponseEntity<RESTError>(new RESTError("Bad ID format."), HttpStatus.BAD_REQUEST);
        }

        Ride ride = rideService.findById(id);
        if (ride == null) {
        	return new ResponseEntity<>("Ride does not exist!", HttpStatus.NOT_FOUND);
        }
        
        final GenericUser user = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        if (userService.isDriver(user)) {
            if (!ride.getDriver().getId().equals(user.getId())) {
            	return new ResponseEntity<>("Ride does not exist!", HttpStatus.NOT_FOUND);
            }
        }
        
        if (ride.getStatus() != Ride.Status.PENDING) {
        	return new ResponseEntity<RESTError>(new RESTError("Cannot accept a ride that is not in status PENDING!"), HttpStatus.BAD_REQUEST);
        }

        rideService.acceptRide(ride);
        driverService.setAvailable(ride.getDriver(), false);

        notifyRidePassengers(ride);
        notifyRideDriver(ride);

        return new ResponseEntity<RideDTO>(to(ride), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('driver')")
    @PutMapping("/{id}/end")
    public ResponseEntity<?> endRide(@PathVariable Long id) {
        if (id == null) {
            return new ResponseEntity<RESTError>(new RESTError("Bad ID format."), HttpStatus.BAD_REQUEST);
        }

        Ride ride = rideService.findById(id);
        if (ride == null) {
        	return new ResponseEntity<>("Ride does not exist!", HttpStatus.NOT_FOUND);
        }
        
        final GenericUser user = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        if (userService.isDriver(user)) {
            if (!ride.getDriver().getId().equals(user.getId())) {
            	return new ResponseEntity<>("Ride does not exist!", HttpStatus.NOT_FOUND);
            }
        }
        
        if (ride.getStatus() != Ride.Status.STARTED) {
        	return new ResponseEntity<RESTError>(new RESTError("Cannot end a ride that is not in status STARTED!"), HttpStatus.BAD_REQUEST);
        }

        rideService.finishRide(ride);
        driverService.setAvailable(ride.getDriver(), true);

        notifyRidePassengers(ride);
        notifyRideDriver(ride);

        return new ResponseEntity<RideDTO>(to(ride), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('driver')")
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> rejectRide(@PathVariable Long id, @RequestBody CancellationBodyDTO reason) {
        if (id == null) {
            return new ResponseEntity<RESTError>(new RESTError("Bad ID format."), HttpStatus.BAD_REQUEST);
        }

        Ride ride = rideService.findById(id);
        if (ride == null) {
        	return new ResponseEntity<>("Ride does not exist!", HttpStatus.NOT_FOUND);
        }
        
        final GenericUser user = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        if (userService.isDriver(user)) {
            if (!ride.getDriver().getId().equals(user.getId())) {
            	return new ResponseEntity<>("Ride does not exist!", HttpStatus.NOT_FOUND);
            }
        }
        
        if (ride.getStatus() != Ride.Status.PENDING && ride.getStatus() != Ride.Status.ACCEPTED) {
        	return new ResponseEntity<RESTError>(new RESTError("Cannot cancel a ride that is not in status PENDING or ACCEPTED!"), HttpStatus.BAD_REQUEST);
        }

        final Cancellation cancellation = cancellationService.create(reason.getReason(), user); 
        rideService.rejectRide(ride, cancellation);
        driverService.setAvailable(ride.getDriver(), true);

        notifyRidePassengers(ride);
        notifyRideDriver(ride);

        return new ResponseEntity<RideDTO>(to(ride), HttpStatus.OK);
    }
    
    @PreAuthorize("hasAnyAuthority('passenger', 'admin')")
    @PostMapping("/favorites")
    public ResponseEntity<?> createFavouriteRoute(@RequestBody FavoriteRouteDTO dto){
		try {
			MyValidator.validateRequired(dto.getFavoriteName(), "favoriteName");
			MyValidator.validateRequired(dto.getVehicleType(), "vehicleType");
			MyValidator.validateRequired(dto.isBabyTransport(), "babyTransport");
			MyValidator.validateRequired(dto.getLocations(), "locations");
			MyValidator.validateRequired(dto.getPassengers(), "passengers");
//			MyValidator.validateRequired(dto.getScheduledTime(), "scheduledTime");
			
			MyValidator.validateRouteDTO(dto.getLocations(), "locations");
			if(dto.getScheduledTime() != null)
				MyValidator.validateDateTime(dto.getScheduledTime(), "scheduledTime");
		} catch (MyValidatorException e1) {
			return new ResponseEntity<RESTError>(new RESTError(e1.getMessage()), HttpStatus.BAD_REQUEST);
		}

    	try {
			 FavoriteRoute favoriteRoute = this.rideService.createFavoriteRoute(dto, 10);
			 return new ResponseEntity<FavoriteRouteDTO>(FavoriteRouteDTO.from(favoriteRoute), HttpStatus.OK);
		} catch (NonExistantVehicleType e) {
			return new ResponseEntity<RESTError>(new RESTError("Vehicle type doesn't exist"), HttpStatus.BAD_REQUEST);
		} catch (NonExistantUserException e) {
			return new ResponseEntity<RESTError>(new RESTError("User doesn't exist"), HttpStatus.BAD_REQUEST);
		} catch (FavoriteRideLimitExceeded e) {
			return new ResponseEntity<RESTError>(new RESTError("Number of favorite rides cannot exceed 10!"), HttpStatus.BAD_REQUEST);
		}
    }
    
    @GetMapping("/favorites")
    public ResponseEntity<?> getFavouriteRoutes(){
    	List<FavoriteRoute> favoriteRoutes = this.rideService.getFavouriteRoutes();
    	List<FavoriteRouteDTO> favoriteRouteDTOs = favoriteRoutes.stream().map(fav -> FavoriteRouteDTO.from(fav)).toList();
    	return new ResponseEntity<List<FavoriteRouteDTO>>(favoriteRouteDTOs, HttpStatus.OK);
    	
    }
    
    @GetMapping("/favorites/passenger/{passengerId}")
    public ResponseEntity<?> getFavouriteRoutesByPassenger(@PathVariable long passengerId){
    	List<FavoriteRoute> favoriteRoutes;
		try {
			favoriteRoutes = this.rideService.getFavouriteRoutesByPassengerId(passengerId);
		} catch (NonExistantUserException e) {
			return new ResponseEntity<RESTError>(new RESTError("Passenger with this id doesn't exist!"), HttpStatus.BAD_REQUEST);
		}
    	List<FavoriteRouteDTO> favoriteRouteDTOs = favoriteRoutes.stream().map(fav -> FavoriteRouteDTO.from(fav)).toList();
    	return new ResponseEntity<List<FavoriteRouteDTO>>(favoriteRouteDTOs, HttpStatus.OK);
    	
    }
    
    @DeleteMapping("/favorites/{id}")
    public ResponseEntity<?> deleteFavouriteRoute(@PathVariable long id){
    	try {
			this.rideService.delete(id);
		} catch (NonExistantFavoriteRoute e) {
			return new ResponseEntity<RESTError>(new RESTError("Favorite route doesn't exist"), HttpStatus.NOT_FOUND);
		}
    	return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    	
    }
    
    @GetMapping("/graph/passenger/{passengerId}")
    public ResponseEntity<?> getPassengerGraphData(@PathVariable Long passengerId, @RequestParam(required = true) String from, @RequestParam(required = true) String to){
		if (passengerId == null) {
			return new ResponseEntity<RESTError>(new RESTError("Field id is required!"), HttpStatus.BAD_REQUEST);
		}
    	LocalDateTime tFrom = null, tTo = null;
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
		try {
			List<GraphEntryDTO>result = this.rideService.getPassengerGraphData(tFrom, tTo, passengerId);
			return new ResponseEntity<List<GraphEntryDTO>>(result, HttpStatus.OK);
		} catch (NonExistantUserException e) {
			return new ResponseEntity<RESTError>(new RESTError("Passenger with that id doesn't exist"), HttpStatus.NOT_FOUND);
		}
    }
    
    @GetMapping("/graph/driver/{driverId}")
    public ResponseEntity<?> getDriverGraphData(@PathVariable Long driverId, @RequestParam(required = true) String from, @RequestParam(required = true) String to){
		if (driverId == null) {
			return new ResponseEntity<RESTError>(new RESTError("Field id is required!"), HttpStatus.BAD_REQUEST);
		}
    	LocalDateTime tFrom = null, tTo = null;
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
		try {
			List<GraphEntryDTO>result = this.rideService.getDrivertGraphData(tFrom, tTo, driverId);
			return new ResponseEntity<List<GraphEntryDTO>>(result, HttpStatus.OK);
		} catch (NonExistantUserException e) {
			return new ResponseEntity<RESTError>(new RESTError("Passenger with that id doesn't exist"), HttpStatus.NOT_FOUND);
		}
    }
    
//    TODO remove
    @GetMapping
    public ResponseEntity<?> getRides(){
    	return new ResponseEntity<List<Ride>>(this.rideService.findAll(), HttpStatus.OK);
     }
    
    @PostMapping("/generate")
    public ResponseEntity<?> generate(@RequestParam Long driverId, @RequestParam Long passengerId){
    	this.rideService.generate(driverId, passengerId);
    	return new ResponseEntity<>(HttpStatus.NO_CONTENT);
     }

    
}

