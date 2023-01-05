package com.shuttle.passenger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.springframework.web.multipart.MultipartFile;

import com.shuttle.common.exception.EmailAlreadyUsedException;
import com.shuttle.common.exception.InvalidBase64Exception;

import jakarta.mail.MessagingException;

public interface IPassengerService {

	PassengerDTO register(PassengerDTO passengerDTO) throws UnsupportedEncodingException, MessagingException, EmailAlreadyUsedException, IOException, InvalidBase64Exception;

	boolean verify(String code);

}
