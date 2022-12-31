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
import com.shuttle.ride.Ride.Status;
import com.shuttle.ride.dto.CreateRideDTO;

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
	
	@Override
	public Ride createRide(Ride ride) {
		rideRepository.save(ride);	
		return ride;
	}

    /**
     * Heuristic method for finding a driver to take ride.
     * @return A driver. Will never be null. 
     * @throws NoAvailableDriverException If no driver can be found.
     */
    @Override
    public Driver findMostSuitableDriver(CreateRideDTO createRideDTO) throws NoAvailableDriverException {
		final List<Driver> activeDrivers = driverRepository.findAllActive();
		if (activeDrivers.size() == 0) {
            // No driver is logged in.
			throw new NoAvailableDriverException();
		}

        // PENDING      ACCEPTED
        //                          -> Suitable (High priority)
        //                 x        -> Suitable (TODO: When to schedule?)
        //    x                     -> Not suitable (has future ride).
        //    x            x        -> Not suitable (busy and has future ride).
        //

        final List<Driver> noPendingNoAccepted = findDriversWithNoPendingNoAccepted().stream().filter(d -> !workedMoreThan8Hours(d)).toList();
        final List<Driver> noPendingYesAccepted = findDriversWithNoPendingYesAccepted().stream().filter(d -> !workedMoreThan8Hours(d)).toList();

        if (noPendingNoAccepted.size() > 0) {
            // Find nearest one.
            return findNearestDriver(noPendingNoAccepted);
        } else if (noPendingYesAccepted.size() > 0) {
            // Find the one that'll finish soon.
            return findDriverAvailableMostSoon(noPendingYesAccepted);
        } else {
            // All logged in drivers have a pending ride. They are busy with a future ride.
            throw new NoAvailableDriverException();
        }
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
    private Driver findNearestDriver(List<Driver> drivers) {
        return drivers.get(0);
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
	public Ride rejectRide(Ride ride) {
		ride.setStatus(Status.Rejected);
		
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
}
