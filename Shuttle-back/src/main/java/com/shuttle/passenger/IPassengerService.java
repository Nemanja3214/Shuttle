package com.shuttle.passenger;

import java.io.UnsupportedEncodingException;

import org.springframework.messaging.MessagingException;

public interface IPassengerService {

	void register(PassengerDTO passengerDTO) throws UnsupportedEncodingException, MessagingException, jakarta.mail.MessagingException;

}
