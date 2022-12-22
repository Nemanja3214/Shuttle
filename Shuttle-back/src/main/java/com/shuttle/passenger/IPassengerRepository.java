package com.shuttle.passenger;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface IPassengerRepository extends CrudRepository<Passenger, Long> {
	@Query("select p from Passenger p where p.credentials.email = ?1")
	public Passenger findByEmail(String myemail);
}
