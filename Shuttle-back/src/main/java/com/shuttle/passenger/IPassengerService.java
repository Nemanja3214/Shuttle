package com.shuttle.passenger;

import java.io.UnsupportedEncodingException;

import com.shuttle.common.exception.EmailAlreadyUsedException;

import jakarta.mail.MessagingException;

public interface IPassengerService {

	PassengerDTO register(PassengerDTO passengerDTO) throws UnsupportedEncodingException, MessagingException, EmailAlreadyUsedException;

	boolean verify(String code);

}
