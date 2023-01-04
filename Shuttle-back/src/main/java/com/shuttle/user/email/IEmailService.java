package com.shuttle.user.email;

import java.io.UnsupportedEncodingException;

import com.shuttle.passenger.Passenger;

import jakarta.mail.MessagingException;

public interface IEmailService {

	void sendVerificationEmail(Passenger passenger, String url)
			throws UnsupportedEncodingException, MessagingException;

}
