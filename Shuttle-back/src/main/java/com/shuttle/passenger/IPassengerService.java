package com.shuttle.passenger;

import java.io.UnsupportedEncodingException;

import jakarta.mail.MessagingException;

public interface IPassengerService {

	void register(PassengerDTO passengerDTO) throws UnsupportedEncodingException, MessagingException;

	boolean verify(String code);

}
