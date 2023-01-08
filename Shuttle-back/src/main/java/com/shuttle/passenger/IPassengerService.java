package com.shuttle.passenger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.data.domain.Pageable;

import com.shuttle.common.exception.EmailAlreadyUsedException;
import com.shuttle.common.exception.NonExistantUserException;
import com.shuttle.common.exception.TokenExpiredException;

import jakarta.mail.MessagingException;

public interface IPassengerService {

	PassengerDTO register(PassengerDTO passengerDTO) throws UnsupportedEncodingException, MessagingException, EmailAlreadyUsedException, IOException;

	boolean verify(String code) throws TokenExpiredException, NonExistantUserException;

	boolean activate(Long activationId) throws NonExistantUserException, TokenExpiredException;

    /**
     * Find passenger by email.
     * @param email The email.
     * @return The passenger with such email or null if none found.
     */
    public Passenger findByEmail(String email);

    /**
     * Find passenger by id.
     * @param passengerId ID.
     * @return The passenger with the provided ID or null if none found.
     */
    public Passenger findById(Long passengerId);

	public List<Passenger> findAll(Pageable pageable);

	public Passenger updatePassenger(Long id, PassengerUpdateDTO newData) throws NonExistantUserException, IOException;

}
