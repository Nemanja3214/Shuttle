package com.shuttle.ride;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.shuttle.driver.Driver;

public interface IRideRepository extends JpaRepository<Ride, Long> {
    public List<Ride> findByDriverAndStatus(Driver driver, Ride.Status status);

    @Query(value = "from Ride r where r.driver.id = :driverId and ( r.endTime BETWEEN :startDate AND :endDate )")
    public Page<Ride> getAllBetweenDates(LocalDateTime startDate, LocalDateTime endDate, Long driverId, Pageable pageable);
}
