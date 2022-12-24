package com.shuttle.passenger;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface IPassengerRepository extends JpaRepository<Passenger, Long> {
	@Query("select p from Passenger p where p.email = ?1")
	public Passenger findByEmail(String myemail);
}
