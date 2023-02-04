package com.shuttle.panic;
import com.shuttle.ride.Ride;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IPanicRepository extends JpaRepository<Panic, Long> {
    public List<Panic> findPanicsByRide(Ride ride);
    
}
