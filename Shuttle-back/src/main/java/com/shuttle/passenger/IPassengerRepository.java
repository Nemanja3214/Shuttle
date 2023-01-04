package com.shuttle.passenger;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


public interface IPassengerRepository extends JpaRepository<Passenger, Long> {
	@Query("select p from Passenger p where p.email = ?1")
	public Passenger findByEmail(String myemail);
	
    @Modifying
    @Query(value="DELETE FROM Passenger p \n"
    		+ " WHERE p.id IN \n"
    		+ " (SELECT t.passenger_id FROM verification_token t WHERE t.expire_date_time < CURRENT_TIMESTAMP)",
    		nativeQuery = true)
    public void deleteByExpiredToken();
}
