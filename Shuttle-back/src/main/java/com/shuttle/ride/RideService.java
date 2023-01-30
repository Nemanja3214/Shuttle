package com.shuttle.ride;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.shuttle.common.exception.FavoriteRideLimitExceeded;
import com.shuttle.common.exception.NonExistantFavoriteRoute;
import com.shuttle.common.exception.NonExistantUserException;
import com.shuttle.common.exception.NonExistantVehicleType;
import com.shuttle.driver.Driver;
import com.shuttle.driver.IDriverRepository;
import com.shuttle.driver.IDriverService;
import com.shuttle.location.FavoriteRoute;
import com.shuttle.location.IFavouriteRouteRepository;
import com.shuttle.location.ILocationRepository;
import com.shuttle.location.Location;
import com.shuttle.location.Route;
import com.shuttle.location.dto.FavoriteRouteDTO;
import com.shuttle.location.dto.LocationDTO;
import com.shuttle.location.dto.RouteDTO;
import com.shuttle.passenger.IPassengerRepository;
import com.shuttle.passenger.Passenger;
import com.shuttle.ride.Ride.Status;
import com.shuttle.ride.cancellation.Cancellation;
import com.shuttle.ride.dto.CreateRideDTO;
import com.shuttle.ride.dto.GraphEntryDTO;
import com.shuttle.user.GenericUser;
import com.shuttle.vehicle.IVehicleService;
import com.shuttle.vehicle.Vehicle;
import com.shuttle.vehicle.vehicleType.IVehicleTypeRepository;
import com.shuttle.vehicle.vehicleType.VehicleType;

import jakarta.transaction.Transactional;

class NoAvailableDriverException extends Throwable {
	private static final long serialVersionUID = -2718176046357707329L;
}

@Service
public class RideService implements IRideService {
	@Autowired
	private IRideRepository rideRepository;
	@Autowired
	private IDriverRepository driverRepository; // TODO: Remove, we have driverService now.
    @Autowired
    private IDriverService driverService;
    @Autowired
    private IVehicleService vehicleService;
    @Autowired
    private IPassengerRepository passengerRepository;
    @Autowired
    private IFavouriteRouteRepository favouriteRouteRepository;
    @Autowired
	private IVehicleTypeRepository vehicleTypeRepository;
    @Autowired
	private ILocationRepository locationRepository;

	@Override
	public Ride save(Ride ride) {
		rideRepository.save(ride);	
		return ride;
	}

    @Override
    public Driver findMostSuitableDriver(CreateRideDTO createRideDTO, boolean forFuture) throws NoAvailableDriverException {
        if (forFuture) {
            try {
                return findMostSuitableDriver(createRideDTO);
            } 
            catch (NoAvailableDriverException e) {
                return null;
            }
        } else {
            return findMostSuitableDriver(createRideDTO);
        }
    }

    private Driver findMostSuitableDriver(CreateRideDTO createRideDTO) throws NoAvailableDriverException {
		final List<Driver> activeDrivers = driverRepository.findAllActive();
		if (activeDrivers.size() == 0) {
            // No driver is logged in.
			throw new NoAvailableDriverException();
		}

        // PENDING      ACCEPTED
        //                          -> Suitable
        //                 x        -> Suitable
        //    x                     -> Not suitable
        //    x            x        -> Not suitable

        VehicleType vt = vehicleService.findVehicleTypeByName(createRideDTO.getVehicleType()).orElse(null);

        final List<Driver> noPendingNoAcceptedNoStarted = findDriversWithNoPendingNoAcceptedNoStarted().stream()
            .filter(d -> !driverService.workedMoreThan8Hours(d))
            .filter(d -> requestParamsMatch(d, createRideDTO.getBabyTransport(), createRideDTO.getBabyTransport(), createRideDTO.getPassengers().size(), vt))
            .toList();
        final List<Driver> noPendingYesAcceptedOrStarted = findDriversWithNoPendingYesAcceptedOrStarted().stream()
            .filter(d -> !driverService.workedMoreThan8Hours(d))
            .filter(d -> requestParamsMatch(d, createRideDTO.getBabyTransport(), createRideDTO.getBabyTransport(), createRideDTO.getPassengers().size(), vt))
            .toList();

        if (noPendingNoAcceptedNoStarted.size() > 0) {
            // Find nearest one.
            return findNearestDriver(noPendingNoAcceptedNoStarted, createRideDTO.getLocations().get(0).getDeparture());
        } else if (noPendingYesAcceptedOrStarted.size() > 0) {
            // Find the one that'll finish soon.
            return findDriverAvailableMostSoon(noPendingYesAcceptedOrStarted);
        } else {
            // All logged in drivers have a current ride and a pending ride.
            throw new NoAvailableDriverException();
        }
    }

    @Override
    public boolean requestParamsMatch(Driver d, boolean baby, boolean pet, int seatsNeeded, VehicleType vehicleType) {
        final Vehicle v = vehicleService.findByDriver(d);
        if (v == null) {
            return false;
        }
        if (!v.getBabyTransport() && baby) {
            return false;
        }
        if (!v.getPetTransport() && pet) {
            return false;
        }
        if (v.getPassengerSeats() < seatsNeeded) {
            return false;
        }
        if (!v.getVehicleType().equals(vehicleType)) {
            return false;
        }
        return true;
    }

    private List<Driver> findDriversWithNoPendingNoAcceptedNoStarted() {
        List<Driver> drivers = new ArrayList<>();

        for (Driver d : driverRepository.findAllActive()) {
            final List<Ride> pending = rideRepository.findByDriverAndStatus(d, Status.PENDING);
            final List<Ride> accepted = rideRepository.findByDriverAndStatus(d, Status.ACCEPTED);
            final List<Ride> started = rideRepository.findByDriverAndStatus(d, Status.STARTED);

            if (pending.size() == 0 && accepted.size() == 0 && accepted.size() == 0)
            drivers.add(d);
        }	
    	return drivers;        
    }

    private List<Driver> findDriversWithNoPendingYesAcceptedOrStarted() {
        List<Driver> drivers = new ArrayList<>();
        
        for (Driver d : driverRepository.findAllActive()) {
            final List<Ride> pending = rideRepository.findByDriverAndStatus(d, Status.PENDING);
            final List<Ride> accepted = rideRepository.findByDriverAndStatus(d, Status.ACCEPTED);
            final List<Ride> started = rideRepository.findByDriverAndStatus(d, Status.STARTED);

            if (pending.size() == 0 && (accepted.size() > 0 || started.size() > 0))
            drivers.add(d);
        }	
    	return drivers;        
    }

   
    /**
     * 
     * @param drivers List of drivers from which to pick.
     * @return Nearest driver (Euclidean distance).
     */
    private Driver findNearestDriver(List<Driver> drivers, LocationDTO point) {
        final List<Vehicle> vehicles = drivers
            .stream()
            .map(d -> vehicleService.findByDriver(d))
            .filter(v -> v != null)
            .toList();

        return vehicles.stream().sorted((v1, v2) -> {
            final Location l1 = v1.getCurrentLocation();
            final Location l2 = v2.getCurrentLocation();

            final Double dy1 = (l1.getLatitude() - point.getLatitude());
            final Double dx1 = (l1.getLongitude() - point.getLongitude());

            final Double dy2 = (l2.getLatitude() - point.getLatitude());
            final Double dx2 = (l2.getLongitude() - point.getLongitude());

            final Double d1 = (dy1 * dy1) + (dx1 * dx1);
            final Double d2 = (dy2 * dy2) + (dx2 * dx2);

            if (d1 > d2) return 1;
            if (d1 < d2) return -1;
            return 0;
        }).findFirst().get().getDriver();
    }

    /**
     * 
     * @param drivers List of drivers from which to pick. They *must* all have an ACCEPTED or STARTED ride.
     * @return Driver who will finish the current ride the soonest.
     */
    private Driver findDriverAvailableMostSoon(List<Driver> drivers) {
        return drivers.stream().sorted((d1, d2) -> {
            final LocalDateTime ldt1 = findCurrentRideByDriverStartedAccepted(d1).getEstimatedEndTime();
            final LocalDateTime ldt2 = findCurrentRideByDriverStartedAccepted(d2).getEstimatedEndTime();
            return ldt1.compareTo(ldt2);
        }).findFirst().get();
    }

	@Override
	public Ride findById(Long id) {
		return rideRepository.findById(id).orElse(null);
	}

	@Override
	public Ride rejectRide(Ride ride, Cancellation cancellation) {
		ride.setStatus(Status.REJECTED);
        ride.setRejection(cancellation);
		
		ride = rideRepository.save(ride);
		return ride;
	}

	@Override
	public Ride findCurrentRideByDriver(Driver driver) {
		List<Ride> li;
		li = rideRepository.findByDriverAndStatus(driver, Status.STARTED);
		if (li.size() != 0) {
			return li.get(0);
		}
		li = rideRepository.findByDriverAndStatus(driver, Status.ACCEPTED);
		if (li.size() != 0) {
			return li.get(0);
		}
		li = rideRepository.findByDriverAndStatus(driver, Status.PENDING);
		if (li.size() != 0) {
			return li.get(0);
		}
		
		return null;
	}

	@Override
	public Ride findCurrentRideByDriverStartedAccepted(Driver driver) {
		List<Ride> li;
		li = rideRepository.findByDriverAndStatus(driver, Status.STARTED);
		if (li.size() != 0) {
			return li.get(0);
		}
		li = rideRepository.findByDriverAndStatus(driver, Status.ACCEPTED);
		if (li.size() != 0) {
			return li.get(0);
		}
		
		return null;
	}

	@Override
	public Ride acceptRide(Ride ride) {
		ride.setStatus(Status.ACCEPTED);

		ride = rideRepository.save(ride);
		return ride;
	}
	
	@Override
	public Ride startRide(Ride ride) {
		ride.setStatus(Status.STARTED);
		ride.setStartTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
		
		ride = rideRepository.save(ride);
		return ride;
	}


	@Override
	public Ride finishRide(Ride ride) {
		ride.setStatus(Status.FINISHED);
		ride.setEndTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
		
		ride = rideRepository.save(ride);
		return ride;
	}

    @Override
    public Ride findCurrentRideByPassenger(Passenger passenger) {
        List<Ride> all = rideRepository.findStartedAcceptedPendingByPassenger(passenger.getId());
        if (all.size() == 0) {
            return null;
        }
        
        
        Ride bestOne = all.get(0);
        for (Ride r : all) {
        	if (r.getStatus() == Status.STARTED) {
        		bestOne = r;
        	} else if (r.getStatus() == Status.ACCEPTED && bestOne.getStatus() != Status.STARTED) {
                bestOne = r;
            }
        }

        return bestOne;
    }

    @Override
    public Ride cancelRide(Ride ride) {
		ride.setStatus(Status.CANCELED);
		//ride.setEndTime(LocalDateTime.now());
		
		ride = rideRepository.save(ride);
		return ride;
    }
    

	@Override
	public Ride panicRide(Ride ride) {
		ride.setStatus(Status.CANCELED);
		ride.setEndTime(LocalDateTime.now());
		
		ride = rideRepository.save(ride);
		return ride;
	}

    @Override
    public List<Ride> findRidesWithNoDriver() {
        return rideRepository.findByDriverNull();
    }

    @Override
    public List<Ride> findAllPendingInFuture() {
        return rideRepository.findPendingInTheFuture();
    }

	@Override
	public List<Ride> findByUser(GenericUser user, Pageable pageable, LocalDateTime from, LocalDateTime to) {
		if (from != null && to != null) {
			return rideRepository.findByUser(user.getId(), pageable, from, to);
		} else {
			return rideRepository.findByUser(user.getId(), pageable);
		}
	}
	public List<Ride> findRidesByPassengerInDateRange(Long passengerId, String from, String to, Pageable pageable) throws NonExistantUserException {
		Optional<Passenger> passengerO = this.passengerRepository.findById(passengerId);
		if(passengerO.isEmpty()) {
			throw new NonExistantUserException();
		}
		Passenger passenger = passengerO.get();
		
		ZonedDateTime zdt;
		
		zdt = ZonedDateTime.parse(from);
		LocalDateTime fromTime = zdt.toLocalDateTime();
		
		zdt = ZonedDateTime.parse(to);
		LocalDateTime toTime = zdt.toLocalDateTime();
		
		return this.rideRepository.getAllByPassengerAndBetweenDates(fromTime, toTime, passenger, pageable);
	}

	@Override
	@Transactional
    public FavoriteRoute createFavoriteRoute(FavoriteRouteDTO dto, long favLimit) throws NonExistantVehicleType, NonExistantUserException, FavoriteRideLimitExceeded {
    	FavoriteRoute favoriteRoute = new FavoriteRoute();
    	
    	Optional<VehicleType> vehicleType = this.vehicleTypeRepository.findVehicleTypeByNameIgnoreCase(dto.getVehicleType());
    	if(vehicleType.isEmpty()) {
    		throw new NonExistantVehicleType();
    	}
    	List<Long> ids = dto.getPassengers().parallelStream().map(passenger -> passenger.getId()).toList();
    	boolean allPassengersExist = ids.parallelStream().allMatch(id -> this.passengerRepository.existsById(id));
    	if(!allPassengersExist) {
    		throw new NonExistantUserException();
    	}
    	
    	Boolean exceededLimit = Boolean.TRUE.equals(this.favouriteRouteRepository.anyPassengerExceededLimit(ids, favLimit));
    	if(exceededLimit == true) {
    		throw new FavoriteRideLimitExceeded();
    	}
        
    	if(dto.getScheduledTime() != null) {
            LocalDateTime scheduledTime = LocalDateTime.parse(dto.getScheduledTime(), DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.of("UTC")));
            favoriteRoute.setScheduledTime(scheduledTime);
    	}
    	
    	List<Passenger> passengers = Collections.unmodifiableList(this.passengerRepository.findAllById(ids));
    	favoriteRoute.setPassengers(passengers);
    	
    	List<Location> locations = RouteDTO.convertToLocations(dto.getLocations());
    	favoriteRoute.setLocations(locations);
    	
    	favoriteRoute.setVehicleType(vehicleType.get());
    	favoriteRoute.setBabyTransport(dto.getBabyTransport());
    	favoriteRoute.setFavoriteName(dto.getFavoriteName());
    	favoriteRoute.setPetTransport(dto.getPetTransport());
    	favoriteRoute = this.favouriteRouteRepository.save(favoriteRoute);
    	return favoriteRoute;
    }
    
    private static List<Location> convertToLocation(List<RouteDTO> routes){
    	return routes.stream().map(route -> route.destination.to()).toList();
    }

	@Override
	public List<FavoriteRoute> getFavouriteRoutes() {
		return this.favouriteRouteRepository.findAll();
	}

	@Override
	public void delete(long id) throws NonExistantFavoriteRoute {
		if(!this.favouriteRouteRepository.existsById(id)) {
			throw new NonExistantFavoriteRoute();
		}
		this.favouriteRouteRepository.deleteById(id);
		
	}

	@Override
	public List<FavoriteRoute> getFavouriteRoutesByPassengerId(long passengerId) throws NonExistantUserException {
		if(!this.passengerRepository.existsById(passengerId)) {
			throw new NonExistantUserException();
		}
		return this.favouriteRouteRepository.findByPassengerId(passengerId);
	}

	@Override
	public void delete(List<Long> routesToDelete) throws NonExistantFavoriteRoute {
		boolean anyNotExists = routesToDelete.stream().anyMatch(id -> !this.favouriteRouteRepository.existsById(id));
		if(anyNotExists) {
			throw new NonExistantFavoriteRoute();
		}
		this.favouriteRouteRepository.deleteAllById(routesToDelete);
		
	}

	@Override
	public List<GraphEntryDTO> getPassengerGraphData(LocalDateTime start, LocalDateTime end, long passengerId) throws NonExistantUserException {
		if(!this.passengerRepository.existsById(passengerId)) {
			throw new NonExistantUserException();
		}
		return this.rideRepository.getPassengerGraphData(start, end, passengerId);
	}
	
	@Override
	public List<GraphEntryDTO> getDriverGraphData(LocalDateTime start, LocalDateTime end, long driverId) throws NonExistantUserException {
		if(!this.driverRepository.existsById(driverId)) {
			throw new NonExistantUserException();
		}
		return this.rideRepository.getDriverGraphData(start, end, driverId);
	}

	@Override
	public void generate(Long driverId, Long passengerId) {
    	for(int i = 0; i < 10; ++i) {
            LocalDateTime start = generateDateTime(true);
            LocalDateTime end = generateDateTime(false);            
            Driver d = this.driverRepository.findById(driverId).get();
            Passenger p = this.passengerRepository.findById(passengerId).get();
            
            List<Passenger> passengers = new ArrayList<>();
            passengers.add(p);
            
            Route route = new Route();
            List<Location> locations = new ArrayList<>();
            route.setLocations(locations);
            
            VehicleType vt = vehicleTypeRepository.findVehicleTypeByNameIgnoreCase("standard").get();
            
     		Ride r = new Ride(null,
     				start, end,
     				new Random().nextDouble(30, 60),
     				d, passengers, route, 12, false, false, vt, null, Status.FINISHED, LocalDateTime.now(), new Random().nextDouble(30, 100));
     		this.rideRepository.save(r);
    	}
		
	}
    
    private LocalDateTime generateDateTime(boolean isStart) {
		Calendar calendar = Calendar.getInstance();
        TimeZone tz = calendar.getTimeZone();
        ZoneId zoneId = tz.toZoneId();     
        
        calendar.add(Calendar.DAY_OF_MONTH, -10);
        LocalDate lowerBound = LocalDateTime.ofInstant(calendar.toInstant(), zoneId).toLocalDate();
        
        calendar.add(Calendar.DAY_OF_MONTH, 20);
        LocalDate upperBound= LocalDateTime.ofInstant(calendar.toInstant(), zoneId).toLocalDate();
        
        if(isStart) {
        	return between(lowerBound, LocalDate.now());
        }
        else {
        	return between(LocalDate.now(), upperBound);
        }
	}

	public static LocalDateTime between(LocalDate startInclusive, LocalDate endExclusive) {
        long startEpochDay = startInclusive.toEpochDay();
        long endEpochDay = endExclusive.toEpochDay();
        long randomDay = ThreadLocalRandom
          .current()
          .nextLong(startEpochDay, endEpochDay);

        return LocalDate.ofEpochDay(randomDay).atStartOfDay();
    }
    

	@Override
	public List<Ride> findAll() {
		return this.rideRepository.findAll();
	}
}
