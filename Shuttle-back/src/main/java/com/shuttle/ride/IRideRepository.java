package com.shuttle.ride;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.shuttle.driver.Driver;
import com.shuttle.passenger.Passenger;

public interface IRideRepository extends JpaRepository<Ride, Long> {
    public List<Ride> findByDriverAndStatus(Driver driver, Ride.Status status);

    @Query(value = "from Ride r where r.driver.id = :driverId and ( r.endTime BETWEEN :startDate AND :endDate )")
    public Page<Ride> getAllBetweenDates(LocalDateTime startDate, LocalDateTime endDate, Long driverId, Pageable pageable);

    // If Ride::Status is stored as an int, it must be an int here, too.
    @Query(value = "from Ride r join r.passengers plist where plist.id = :passengerId and r.status in (0, 1)")
    public List<Ride> findActiveOrPendingByPassengerId(Long passengerId);

    /**
     * @return List of Ride objects whose Driver is null.
     */
    public List<Ride> findByDriverNull();

    @Query(value = "from Ride r where r.status = 0 and r.startTime > current_time")
    public List<Ride> findPendingInTheFuture();
    
    @Query(value = "from Ride r where (:passenger) IN elements(r.passengers) and ( r.endTime BETWEEN :startDate AND :endDate )")
    public List<Ride> getAllByPassengerAndBetweenDates(LocalDateTime startDate, LocalDateTime endDate, Passenger passenger, Pageable pageable);

}
