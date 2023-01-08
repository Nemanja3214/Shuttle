package com.shuttle.passenger;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface IPassengerRepository extends JpaRepository<Passenger, Long> {
	@Query("select p from Passenger p where p.email = :email")
	public Passenger findByEmail(String email);
	
    @Modifying
    @Query(value="DELETE FROM Passenger p \n"
    		+ " WHERE p.id IN \n"
    		+ " (SELECT t.passenger_id FROM verification_token t WHERE t.expire_date_time < CURRENT_TIMESTAMP)",
    		nativeQuery = true)
    public void deleteByExpiredToken();
    
    @Query("SELECT CASE"
    		+ " WHEN COUNT(p) > 0 THEN true "
    		+ "ELSE false END "
    		+ "FROM Passenger p WHERE LOWER(p.email) = LOWER(:email)")
    Boolean existsByEmail(@Param("email") String email);
}
