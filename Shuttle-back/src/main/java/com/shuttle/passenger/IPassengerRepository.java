package com.shuttle.passenger;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IPassengerRepository extends JpaRepository<Passenger, Long> {
}
