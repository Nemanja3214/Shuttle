package com.shuttle.passenger;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.MessagingException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shuttle.user.email.IEmailService;
import com.shuttle.verificationToken.VerificationToken;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class PassengerService implements IPassengerService{
	@Autowired
    private PasswordEncoder passwordEncoder;
	
	@Autowired
	IPassengerRepository passengerRepository;
	
	@Autowired
	IEmailService emailService;

	@Override
	public void register(PassengerDTO passengerDTO) throws UnsupportedEncodingException, MessagingException, jakarta.mail.MessagingException {
		Passenger newPassenger = new Passenger();
		VerificationToken token = new VerificationToken();
		
		String encodedPassword = passwordEncoder.encode(passengerDTO.password);
		newPassenger.setPassword(encodedPassword);
	     
		
	    String randomCode = makeRandomString(64);
	    token.setToken(randomCode);
	    
	    Calendar calendar = Calendar.getInstance();
	    calendar.add(Calendar.DAY_OF_MONTH, 1);
        TimeZone tz = calendar.getTimeZone();
        ZoneId zoneId = tz.toZoneId();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(calendar.toInstant(), zoneId);
	    token.setExpireDateTime(localDateTime);
	    
	    token.setUser(newPassenger);
	    
//	    newPassenger.setVerificationToken(token);
	    newPassenger.setEnabled(false);
	     
	    passengerRepository.save(newPassenger);
	     
	    emailService.sendVerificationEmail(newPassenger, "www.nesto.com");	
		
	}

	private String makeRandomString(int i) {
		byte[] array = new byte[i];
	    new Random().nextBytes(array);
	    String generatedString = new String(array, Charset.forName("UTF-8"));
	    return generatedString;
	}
	


}
