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
import com.shuttle.user.dto.UserDTO;
import com.shuttle.vehicle.IVehicleTypeRepository;
import com.shuttle.vehicle.VehicleType;

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

    @MessageMapping("/ride/driver/{driverId}")
    public void driverFetchRide(@DestinationVariable Long driverId) {
        if (driverId == null) {
            return;
        }

        final Driver driver = driverService.get(driverId);
        if (driver == null) {
            return;
        }

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

                if (rideService.requestParamsMatch(driver, ride.getBabyTransport(), ride.getPetTransport(), ride.getPassengers().size(), ride.getVehicleType())) {
                    ride.setDriver(driver);
                    rideService.save(ride);
                } else {
                    return;
                }
            } else {
                return;
            }   
        }

        final String dest = String.format("/ride/driver/%d", driverId.longValue());

        if (ride.getDriver().isAvailable()) {
            driverService.setAvailable(ride.getDriver(), false);
        }
        
        template.convertAndSend(dest, to(ride));
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
        System.out.println(passenger);

        final Ride ride = rideService.findActiveOrPendingByPassenger(passenger);
        final String dest = String.format("/ride/passenger/%d", passengerId.longValue());
        if (ride == null) {
            template.convertAndSend(dest, (Void) null);
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

    @PostMapping
    public ResponseEntity<?> createRide(@RequestBody CreateRideDTO createRideDTO) {
        try {
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

    @GetMapping("/driver/{driverId}/active")
    public ResponseEntity<RideDTO> getActiveRideByDriver(@PathVariable long driverId) {
        final Driver driver = driverService.get(driverId);

        if (driver == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } else {
            Ride ride = rideService.findCurrentRideByDriver(driver);

            if (ride == null) {
                return new ResponseEntity<>(null, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(to(ride), HttpStatus.OK);
            }
        }
    }

    @GetMapping("/passenger/{passengerId}/active")
    public ResponseEntity<?> getActiveRideByPassenger(@PathVariable Long passengerId) {
        if (passengerId == null) {
            return new ResponseEntity<RESTError>(new RESTError("Bad ID format."), HttpStatus.BAD_REQUEST);
        }

        final Passenger passenger = passengerService.findById(passengerId);
        if (passenger == null) {
            return new ResponseEntity<RESTError>(new RESTError("Passenger not found."), HttpStatus.NOT_FOUND);
        }

        Ride ride = rideService.findActiveOrPendingByPassenger(passenger);
        if (ride == null) {
            return new ResponseEntity<>(null, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(to(ride), HttpStatus.OK);
        }

        // return new ResponseEntity<RideDTO>(new RideDTO(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RideDTO> getRide(@PathVariable long id) {
        return new ResponseEntity<RideDTO>(new RideDTO(), HttpStatus.OK);
    }

    @PutMapping("/{id}/withdraw")
    public ResponseEntity<?> withdrawRide(@PathVariable Long id) {
        if (id == null) {
            return new ResponseEntity<RESTError>(new RESTError("Bad ID format."), HttpStatus.BAD_REQUEST);
        }

        Ride ride = rideService.findById(id);
        if (ride == null) {
            return new ResponseEntity<>(null, HttpStatus.OK);
        }

        if (ride.getStatus() != Status.Pending) {
            return new ResponseEntity<RESTError>(new RESTError("Cannot cancel a ride that isn't pending."), HttpStatus.BAD_REQUEST);
        }

        this.rideService.cancelRide(ride);

        if (ride.getDriver() != null) {
            driverService.setAvailable(ride.getDriver(), true);
            notifyRideDriver(ride);
        }

        notifyRidePassengers(ride);

        return new ResponseEntity<RideDTO>(to(ride), HttpStatus.OK);
    }

    @PutMapping("/{id}/panic")
    public ResponseEntity<?> panicRide(@PathVariable Long id, @RequestBody String reason) {   
        if (id == null) {
            return new ResponseEntity<Void>((Void)null, HttpStatus.BAD_REQUEST);
        }
        
        Ride ride = rideService.findById(id);   
        if (ride == null) {
            return new ResponseEntity<RESTError>(new RESTError("Ride does not exist!"), HttpStatus.NOT_FOUND);
        }

        Panic p = panicService.add(ride, null, reason);

        PanicDTO dto = new PanicDTO();
        dto.setReason(p.getReason());
        dto.setTime(p.getTime().toString());
        dto.setId(p.getId());
        dto.setRide(to(ride));
        dto.setUser(new UserDTO()); // TODO: User from JWT.

        rideService.cancelRide(ride);
        driverService.setAvailable(ride.getDriver(), true);

        notifyRideDriver(ride);
        notifyRidePassengers(ride);

        return new ResponseEntity<PanicDTO>(dto, HttpStatus.OK);
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<?> acceptRide(@PathVariable Long id) {
        if (id == null) {
            return new ResponseEntity<RESTError>(new RESTError("Bad ID format."), HttpStatus.BAD_REQUEST);
        }

        Ride ride = rideService.findById(id);
        if (ride == null) {
            return new ResponseEntity<Void>((Void) null, HttpStatus.NOT_FOUND);
        }

        rideService.acceptRide(ride);
        driverService.setAvailable(ride.getDriver(), false);

        notifyRidePassengers(ride);

        return new ResponseEntity<RideDTO>(to(ride), HttpStatus.OK);
    }

    @PutMapping("/{id}/end")
    public ResponseEntity<?> endRide(@PathVariable Long id) {
        if (id == null) {
            return new ResponseEntity<RESTError>(new RESTError("Bad ID format."), HttpStatus.BAD_REQUEST);
        }

        Ride ride = rideService.findById(id);
        if (ride == null) {
            return new ResponseEntity<Void>((Void) null, HttpStatus.NOT_FOUND);
        }

        rideService.finishRide(ride);
        driverService.setAvailable(ride.getDriver(), true);

        notifyRidePassengers(ride);

        return new ResponseEntity<RideDTO>(to(ride), HttpStatus.OK);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> rejectRide(@PathVariable Long id, @RequestBody CancellationBodyDTO reason) {
        if (id == null) {
            return new ResponseEntity<RESTError>(new RESTError("Bad ID format."), HttpStatus.BAD_REQUEST);
        }

        Ride ride = rideService.findById(id);
        if (ride == null) {
            return new ResponseEntity<Void>((Void) null, HttpStatus.NOT_FOUND);
        }

        driverService.setAvailable(ride.getDriver(), true);
        final Cancellation cancellation = cancellationService.create(reason.getReason(), null); // TODO: Creator.
        rideService.rejectRide(ride, cancellation);

        notifyRidePassengers(ride);

        return new ResponseEntity<RideDTO>(to(ride), HttpStatus.OK);
    }

}
