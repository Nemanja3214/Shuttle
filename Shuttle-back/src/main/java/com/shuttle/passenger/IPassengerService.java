package com.shuttle.passenger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.springframework.web.multipart.MultipartFile;

import com.shuttle.common.exception.EmailAlreadyUsedException;

import jakarta.mail.MessagingException;

public interface IPassengerService {

	PassengerDTO register(PassengerDTO passengerDTO, MultipartFile picture) throws UnsupportedEncodingException, MessagingException, EmailAlreadyUsedException, IOException;

	boolean verify(String code);

}
