package com.shuttle.user.email;

import java.io.UnsupportedEncodingException;

import com.shuttle.passenger.Passenger;

import jakarta.mail.MessagingException;

public interface IEmailService {

	void sendVerificationEmail(Passenger newPassenger, String string) throws UnsupportedEncodingException, org.springframework.messaging.MessagingException, MessagingException;

	void sendDummyMessage() throws UnsupportedEncodingException, MessagingException;

}
