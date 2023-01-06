package com.shuttle.ride;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shuttle.common.RESTError;
import com.shuttle.driver.Driver;
import com.shuttle.driver.IDriverService;
import com.shuttle.location.ILocationService;
import com.shuttle.location.Location;
import com.shuttle.location.Route;
import com.shuttle.location.dto.LocationDTO;
import com.shuttle.location.dto.RouteDTO;
import com.shuttle.panic.IPanicService;
import com.shuttle.panic.Panic;
import com.shuttle.panic.PanicDTO;
import com.shuttle.passenger.IPassengerRepository;
import com.shuttle.passenger.IPassengerService;
import com.shuttle.passenger.Passenger;
import com.shuttle.ride.Ride.Status;
import com.shuttle.ride.cancellation.Cancellation;
import com.shuttle.ride.cancellation.ICancellationService;
import com.shuttle.ride.cancellation.dto.CancellationBodyDTO;
import com.shuttle.ride.cancellation.dto.CancellationDTO;
import com.shuttle.ride.dto.CreateRideDTO;
import com.shuttle.ride.dto.RideDTO;
import com.shuttle.ride.dto.RideDriverDTO;
import com.shuttle.ride.dto.RidePassengerDTO;
import com.shuttle.security.Role;
import com.shuttle.user.GenericUser;
import com.shuttle.user.UserService;
import com.shuttle.user.dto.UserDTO;
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
        final Double distance = 0.5; // TODO: Where to get total distance from?
        final Double velocity = 60.0 / 1000.0; // TODO: Where to get average vehicle velocity from?

        final VehicleType vehicleType = vehicleTypeRepository
                .findVehicleTypeByName(rideDTO.getVehicleType())
                .orElseThrow();
        final Double cost = (vehicleType.getPricePerKM() + 120) * distance;

        final List<Passenger> passengers = rideDTO.getPassengers()
                .stream()
                .map(userInfo -> passengerRepository.findByEmail(userInfo.getEmail()))
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
        r.setStatus(Status.Pending);
        r.setTotalCost(cost);
        r.setDriver(driver);
        r.setVehicleType(vehicleType);
        r.setBabyTransport(rideDTO.isBabyTransport());
        r.setPetTransport(rideDTO.isPetTransport());
        r.setEstimatedTimeInMinutes((int) ((distance / velocity) * 60));
        r.setPassengers(passengers);
        r.setRoute(route);

        return r;
    }

    /**
     * DTO Mapper function
     * @param ride Model object.
     * @return DTO made from the ride.
     */
    public RideDTO to(Ride ride) {
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

        final Ride ride = rideService.findActiveOrPendingByPassenger(passenger);
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
        try {
            final Passenger p = (Passenger)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());

            if (rideService.findActiveOrPendingByPassenger(p) != null) {
                return new ResponseEntity<RESTError>(new RESTError("Cannot create a ride while you have one already pending!"), HttpStatus.BAD_REQUEST);
            }

            final boolean forFuture = createRideDTO.getHour() != null && createRideDTO.getMinute() != null;
            final Driver driver = rideService.findMostSuitableDriver(createRideDTO, forFuture);
            final Ride ride = from(createRideDTO, driver);
   
            if (forFuture) {
                final int h = Integer.valueOf(createRideDTO.getHour());
                final int m = Integer.valueOf(createRideDTO.getMinute());
                final LocalDateTime start = LocalDateTime.now()
                    .withHour(h)
                    .withMinute(m)
                    .withSecond(0);
                ride.setStartTime(start);
            }

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
    	
        final Driver driver = driverService.get(driverId);

        if (driver == null) {
            return new ResponseEntity<>(new RESTError("Driver not found."), HttpStatus.NOT_FOUND);
        } 
        
        final GenericUser user = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        if (userService.isAdmin(user)) {
	    } else if (userService.isDriver(user)) {
            if (user.getId() != driverId) {
                return new ResponseEntity<RESTError>(new RESTError("Active ride does not exist!"), HttpStatus.NOT_FOUND);
            }
        }
            
       Ride ride = rideService.findCurrentRideByDriver(driver);
       if (ride == null) {
           return new ResponseEntity<RESTError>(new RESTError("Active ride does not exist!"), HttpStatus.NOT_FOUND);
       }
       
       return new ResponseEntity<>(to(ride), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('admin', 'passenger')")
    @GetMapping("/passenger/{passengerId}/active")
    public ResponseEntity<?> getActiveRideByPassenger(@PathVariable Long passengerId) {
        if (passengerId == null) {
            return new ResponseEntity<RESTError>(new RESTError("Bad ID format."), HttpStatus.BAD_REQUEST);
        }
        

        final Passenger passenger = passengerService.findById(passengerId);
        if (passenger == null) {
            return new ResponseEntity<RESTError>(new RESTError("Passenger not found."), HttpStatus.NOT_FOUND);
        }

        final GenericUser user = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        if (userService.isAdmin(user)) {
	    } else if (userService.isPassenger(user)) {
            if (user.getId() != passengerId) {
                return new ResponseEntity<RESTError>(new RESTError("Active ride does not exist!"), HttpStatus.NOT_FOUND);
            }
        }

        Ride ride = rideService.findActiveOrPendingByPassenger(passenger);
        if (ride == null) {
            return new ResponseEntity<RESTError>(new RESTError("Active ride does not exist!"), HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<>(to(ride), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('admin', 'passenger', 'driver')")
    @GetMapping("/{rideId}")
    public ResponseEntity<?> getRide(@PathVariable Long rideId) {
    	System.out.println("AAAAAAAAAAAAAAAAAAAAAAA");
    	if (rideId == null) {
            return new ResponseEntity<RESTError>(new RESTError("Bad ID format."), HttpStatus.BAD_REQUEST);
        }

        final Ride ride = rideService.findById(rideId);
        if (ride == null) {
            return new ResponseEntity<RESTError>(new RESTError("Ride does not exist!"), HttpStatus.NOT_FOUND);
        }
        
        final GenericUser user = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        if (userService.isAdmin(user)) {	
	    } else if (userService.isPassenger(user)) {
	    	if (ride.getPassengers().stream().noneMatch(p -> p.getId().equals(user.getId()))) {
                return new ResponseEntity<RESTError>(new RESTError("Ride does not exist!"), HttpStatus.NOT_FOUND);
            }	
	    } else if (userService.isDriver(user)) {
	    	System.out.println(ride.getDriver().getId() + " " + user.getId());
            if (!ride.getDriver().getId().equals(user.getId())) {
            	return new ResponseEntity<RESTError>(new RESTError("Ride does not exist!"), HttpStatus.NOT_FOUND);
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
            return new ResponseEntity<>(new RESTError("Ride does not exist!"), HttpStatus.NOT_FOUND);
        }
        
        final GenericUser user = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        if (userService.isPassenger(user)) {
	    	if (ride.getPassengers().stream().noneMatch(p -> p.getId().equals(user.getId()))) {
                return new ResponseEntity<RESTError>(new RESTError("Ride does not exist!"), HttpStatus.NOT_FOUND);
            }	
	    }

        if (ride.getStatus() != Status.Pending) {
            return new ResponseEntity<RESTError>(new RESTError("Cannot cancel a ride that isn't pending."), HttpStatus.BAD_REQUEST);
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
    public ResponseEntity<?> panicRide(@PathVariable Long id, @RequestBody String reason) {   
        if (id == null) {
            return new ResponseEntity<Void>((Void)null, HttpStatus.BAD_REQUEST);
        }
        
        Ride ride = rideService.findById(id);   
        if (ride == null) {
            return new ResponseEntity<RESTError>(new RESTError("Ride does not exist!"), HttpStatus.NOT_FOUND);
        }
        
        final GenericUser user = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        if (userService.isPassenger(user)) {
	    	if (ride.getPassengers().stream().noneMatch(p -> p.getId().equals(user.getId()))) {
                return new ResponseEntity<RESTError>(new RESTError("Ride does not exist!"), HttpStatus.NOT_FOUND);
            }	
	    } else if (userService.isDriver(user)) {
            if (!ride.getDriver().getId().equals(user.getId())) {
            	return new ResponseEntity<RESTError>(new RESTError("Ride does not exist!"), HttpStatus.NOT_FOUND);
            }
        }

        rideService.cancelRide(ride);
        driverService.setAvailable(ride.getDriver(), true);
        
        Panic p = panicService.add(ride, user, reason);
        PanicDTO dto = new PanicDTO();
        dto.setReason(p.getReason());
        dto.setTime(p.getTime().toString());
        dto.setId(p.getId());
        dto.setRide(to(ride));
        dto.setUser(new UserDTO(p.getUser()));

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
        	return new ResponseEntity<RESTError>(new RESTError("Ride does not exist!"), HttpStatus.NOT_FOUND);
        }

        final GenericUser user = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        if (userService.isDriver(user)) {
            if (!ride.getDriver().getId().equals(user.getId())) {
            	return new ResponseEntity<RESTError>(new RESTError("Ride does not exist!"), HttpStatus.NOT_FOUND);
            }
        }
        
        if (ride.getStatus() != Ride.Status.Accepted) {
        	return new ResponseEntity<RESTError>(new RESTError("Cannot start a ride that is not in status ACCEPTED!"), HttpStatus.BAD_REQUEST);
        }

        // TODO: What's the purpose of this endpoint?
        //rideService.acceptRide(ride);
        //driverService.setAvailable(ride.getDriver(), false);
        //notifyRidePassengers(ride);
        //notifyRideDriver(ride);
        
        RideDTO dto = to(ride);
        dto.setStatus(Ride.Status.Started);
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
        	return new ResponseEntity<RESTError>(new RESTError("Ride does not exist!"), HttpStatus.NOT_FOUND);
        }
        
        final GenericUser user = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        if (userService.isDriver(user)) {
            if (!ride.getDriver().getId().equals(user.getId())) {
            	return new ResponseEntity<RESTError>(new RESTError("Ride does not exist!"), HttpStatus.NOT_FOUND);
            }
        }
        
        if (ride.getStatus() != Ride.Status.Pending) {
        	return new ResponseEntity<RESTError>(new RESTError("Cannot start a ride that is not in status PENDING!"), HttpStatus.BAD_REQUEST);
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
        	return new ResponseEntity<RESTError>(new RESTError("Ride does not exist!"), HttpStatus.NOT_FOUND);
        }
        
        final GenericUser user = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        if (userService.isDriver(user)) {
            if (!ride.getDriver().getId().equals(user.getId())) {
            	return new ResponseEntity<RESTError>(new RESTError("Ride does not exist!"), HttpStatus.NOT_FOUND);
            }
        }
        
        if (ride.getStatus() != Ride.Status.Accepted && ride.getStatus() != Ride.Status.Started) {
        	return new ResponseEntity<RESTError>(new RESTError("Cannot start a ride that is not in status STARTED!"), HttpStatus.BAD_REQUEST);
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
        	return new ResponseEntity<RESTError>(new RESTError("Ride does not exist!"), HttpStatus.NOT_FOUND);
        }
        
        final GenericUser user = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        if (userService.isDriver(user)) {
            if (!ride.getDriver().getId().equals(user.getId())) {
            	return new ResponseEntity<RESTError>(new RESTError("Ride does not exist!"), HttpStatus.NOT_FOUND);
            }
        }
        
        if (ride.getStatus() != Ride.Status.Pending) {
        	return new ResponseEntity<RESTError>(new RESTError("Cannot start a ride that is not in status PENDING!"), HttpStatus.BAD_REQUEST);
        }

        final Cancellation cancellation = cancellationService.create(reason.getReason(), user); 
        rideService.rejectRide(ride, cancellation);
        driverService.setAvailable(ride.getDriver(), true);

        notifyRidePassengers(ride);
        notifyRideDriver(ride);

        return new ResponseEntity<RideDTO>(to(ride), HttpStatus.OK);
    }
}
