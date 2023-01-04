package com.shuttle.verificationToken;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shuttle.passenger.Passenger;

public interface IVerificationRepository extends JpaRepository<VerificationToken, Long>{

	public VerificationToken findByToken(String verificationCode);

	public void deleteByPassenger(Passenger passenger);

}
