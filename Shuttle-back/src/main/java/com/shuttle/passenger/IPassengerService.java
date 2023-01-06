package com.shuttle.passenger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.shuttle.common.exception.EmailAlreadyUsedException;
import com.shuttle.common.exception.NonExistantUserException;
import com.shuttle.common.exception.TokenExpiredException;

import jakarta.mail.MessagingException;

public interface IPassengerService {

	PassengerDTO register(PassengerDTO passengerDTO) throws UnsupportedEncodingException, MessagingException, EmailAlreadyUsedException, IOException;

	boolean verify(String code) throws TokenExpiredException, NonExistantUserException;

	boolean activate(Long activationId) throws NonExistantUserException, TokenExpiredException;

}
