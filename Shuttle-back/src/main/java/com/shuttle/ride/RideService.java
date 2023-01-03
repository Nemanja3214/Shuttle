package com.shuttle.ride;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shuttle.driver.Driver;
import com.shuttle.driver.IDriverRepository;
import com.shuttle.driver.IDriverService;
import com.shuttle.location.Location;
import com.shuttle.location.dto.LocationDTO;
import com.shuttle.passenger.Passenger;
import com.shuttle.ride.Ride.Status;
import com.shuttle.ride.cancellation.Cancellation;
import com.shuttle.ride.dto.CreateRideDTO;
import com.shuttle.vehicle.IVehicleService;
import com.shuttle.vehicle.Vehicle;

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

        final List<Driver> noPendingNoAccepted = findDriversWithNoPendingNoAccepted().stream()
            .filter(d -> !workedMoreThan8Hours(d))
            .filter(d -> requestParamsMatch(d, createRideDTO.isBabyTransport(), createRideDTO.isPetTransport(), createRideDTO.getPassengers().size()))
            .toList();
        final List<Driver> noPendingYesAccepted = findDriversWithNoPendingYesAccepted().stream()
            .filter(d -> !workedMoreThan8Hours(d))
            .filter(d -> requestParamsMatch(d, createRideDTO.isBabyTransport(), createRideDTO.isPetTransport(), createRideDTO.getPassengers().size()))
            .toList();

        if (noPendingNoAccepted.size() > 0) {
            // Find nearest one.
            return findNearestDriver(noPendingNoAccepted, createRideDTO.getLocations().get(0).getDeparture());
        } else if (noPendingYesAccepted.size() > 0) {
            // Find the one that'll finish soon.
            return findDriverAvailableMostSoon(noPendingYesAccepted);
        } else {
            // All logged in drivers have a pending ride. They are busy with a future ride.
            throw new NoAvailableDriverException();
        }
    }

    /**
     * Extracted predicate method for determining if the driver's vehicle is suitable for the
     * given parameters.
     * @param d The driver. Must not be null.
     * @param baby True if the driver must be able to transport babies.
     * @param pet  True if the driver must be able to transport pets.
     * @param seatsNeeded Minumum number of passenger seats the driver's vehicle must have.
     * @return True if satisfies all criteria, false otherwise.
     */
    private boolean requestParamsMatch(Driver d, boolean baby, boolean pet, int seatsNeeded) {
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
        return true;
    }

    /**
     * @return Logged-in drivers with no pending rides and no accepted rides.  
     */
    private List<Driver> findDriversWithNoPendingNoAccepted() {
        List<Driver> drivers = new ArrayList<>();

        for (Driver d : driverRepository.findAllActive()) {
            final List<Ride> pending = rideRepository.findByDriverAndStatus(d, Status.Pending);
            final List<Ride> accepted = rideRepository.findByDriverAndStatus(d, Status.Accepted);

            if (pending.size() == 0 && accepted.size() == 0)
            drivers.add(d);
        }	
    	return drivers;        
    }

    /**
     * @return Logged-in drivers with no pending rides but with accepted rides.  
     */
    private List<Driver> findDriversWithNoPendingYesAccepted() {
        List<Driver> drivers = new ArrayList<>();
        
        for (Driver d : driverRepository.findAllActive()) {
            final List<Ride> pending = rideRepository.findByDriverAndStatus(d, Status.Pending);
            final List<Ride> accepted = rideRepository.findByDriverAndStatus(d, Status.Accepted);

            if (pending.size() == 0 && accepted.size() > 0)
            drivers.add(d);
        }	
    	return drivers;        
    }

    /**
     * Helper function to check whether the driver has worked more than 8 hours in the last 24 hours.
     */
    private boolean workedMoreThan8Hours(Driver d) {
        Duration dur = driverService.getDurationOfWorkInTheLast24Hours(d);
        return (dur.compareTo(Duration.ofHours(8)) > 0);
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
     * @param drivers List of drivers from which to pick. They *must* all have an ACCEPTED ride.
     * @return Driver who will finish the current ride the soonest.
     */
    private Driver findDriverAvailableMostSoon(List<Driver> drivers) {
        return drivers.stream().sorted((d1, d2) -> {
            final LocalDateTime ldt1 = findCurrentRideByDriverInProgress(d1).getEstimatedEndTime();
            final LocalDateTime ldt2 = findCurrentRideByDriverInProgress(d2).getEstimatedEndTime();
            return ldt1.compareTo(ldt2);
        }).findFirst().get();
    }

	@Override
	public Ride findById(Long id) {
		return rideRepository.findById(id).orElse(null);
	}

	@Override
	public Ride rejectRide(Ride ride, Cancellation cancellation) {
		ride.setStatus(Status.Rejected);
        ride.setRejection(cancellation);
		
		ride = rideRepository.save(ride);
		return ride;
	}

	@Override
	public Ride findCurrentRideByDriver(Driver driver) {
		List<Ride> accepted = rideRepository.findByDriverAndStatus(driver, Status.Accepted);
		List<Ride> pending = rideRepository.findByDriverAndStatus(driver, Status.Pending);
	
        if (accepted.size() != 0) {
			return accepted.get(0);
		}
		if (pending.size() != 0) {
			return pending.get(0);
		}
		
		return null;
	}

	@Override
	public Ride findCurrentRideByDriverInProgress(Driver driver) {
		List<Ride> active = rideRepository.findByDriverAndStatus(driver, Status.Accepted);
		if (active.size() != 0) {
			return active.get(0);
		}
		
		return null;
	}

	@Override
	public Ride acceptRide(Ride ride) {
		ride.setStatus(Status.Accepted);
		ride.setStartTime(LocalDateTime.now());
		
		ride = rideRepository.save(ride);
		return ride;
	}

	@Override
	public Ride finishRide(Ride ride) {
		ride.setStatus(Status.Finished);
		ride.setEndTime(LocalDateTime.now());
		
		ride = rideRepository.save(ride);
		return ride;
	}

    @Override
    public Ride findActiveOrPendingByPassenger(Passenger passenger) {
        List<Ride> all = rideRepository.findActiveOrPendingByPassengerId(passenger.getId());
        if (all.size() == 0) {
            return null;
        }
        
        Ride bestOne = all.get(0);
        for (Ride r : all) {
            if (r.getStatus() == Status.Accepted) {
                bestOne = r;
            }
        }

        return bestOne;
    }

    @Override
    public Ride cancelRide(Ride ride) {
		ride.setStatus(Status.Canceled);
		ride.setEndTime(LocalDateTime.now());
		
		ride = rideRepository.save(ride);
		return ride;
    }

    @Override
    public List<Ride> findRidesWithNoDriver() {
        return rideRepository.findByDriverNull();
    }
}
