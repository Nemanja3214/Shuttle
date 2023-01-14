package com.shuttle.user.email;

import java.io.UnsupportedEncodingException;

import com.shuttle.passenger.Passenger;
import com.shuttle.user.GenericUser;
import com.shuttle.user.passwordReset.PasswordResetCode;

import jakarta.mail.MessagingException;

public interface IEmailService {
	void sendVerificationEmail(Passenger passenger, String url) throws UnsupportedEncodingException, MessagingException;
	void sendPasswordResetEmail(PasswordResetCode prc) throws UnsupportedEncodingException, jakarta.mail.MessagingException;
}
