package com.shuttle.passenger;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.shuttle.common.FileUploadUtil;
import com.shuttle.common.exception.EmailAlreadyUsedException;
import com.shuttle.security.IRoleRepository;
import com.shuttle.security.Role;
import com.shuttle.security.jwt.JwtTokenUtil;
import com.shuttle.user.email.IEmailService;
import com.shuttle.verificationToken.IVerificationRepository;
import com.shuttle.verificationToken.VerificationToken;

import jakarta.mail.MessagingException;

@Service
public class PassengerService implements IPassengerService{
	@Autowired
    private PasswordEncoder passwordEncoder;
	
	@Autowired
	private IPassengerRepository passengerRepository;
	
	@Autowired
	private IEmailService emailService;
	
	@Autowired
	private IVerificationRepository tokenRepository;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Autowired
	private IRoleRepository roleRepository;
	
	@Override
	public PassengerDTO register(PassengerDTO passengerDTO, MultipartFile picture) throws MessagingException, EmailAlreadyUsedException, IOException {
		if(passengerRepository.existsByEmail(passengerDTO.email)) {
			throw new EmailAlreadyUsedException();
		}
		Passenger newPassenger = PassengerDTO.from(passengerDTO);
		newPassenger.setActive(false);
		newPassenger.setBlocked(false);
		newPassenger.setEnabled(false);
		newPassenger.setFinance((double)0);
		List<Role> passengerRole = roleRepository.findByName("passenger");
		newPassenger.setRoles(passengerRole);
		
		String encodedPassword = passwordEncoder.encode(passengerDTO.password);
		newPassenger.setPassword(encodedPassword);
	     
		VerificationToken token = createToken();
	    
	    newPassenger.setEnabled(false);
	    token.setPassenger(newPassenger);
	     
	    newPassenger = passengerRepository.save(newPassenger);
	    tokenRepository.save(token);
	    
		String uploadDir = "user-photos/" + newPassenger.getId();
        FileUploadUtil.saveFile(uploadDir, passengerDTO.getProfilePicture(), picture);
	     
	    emailService.sendVerificationEmail(newPassenger, "http://localhost:8080/api/passenger/verify?token=" + token.getToken());	
	    return new PassengerDTO(newPassenger);
		
	}

	private VerificationToken createToken() {
		VerificationToken token = new VerificationToken();
		
	    String randomCode = makeRandomString(64);
	    token.setToken(randomCode);
	    
	    Calendar calendar = Calendar.getInstance();
	    calendar.add(Calendar.DAY_OF_MONTH, 1);
        TimeZone tz = calendar.getTimeZone();
        ZoneId zoneId = tz.toZoneId();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(calendar.toInstant(), zoneId);
	    token.setExpireDateTime(localDateTime);
	    return token;
	   
	}

	private String makeRandomString(int i) {
		SecureRandom random = new SecureRandom();
	    byte bytes[] = new byte[128];
	    random.nextBytes(bytes);
	    Encoder encoder = Base64.getUrlEncoder().withoutPadding();
	    String token = encoder.encodeToString(bytes);
	    return token;
	}

	@Override
	@Transactional
	public boolean verify(String verificationCode) {
	   VerificationToken token = tokenRepository.findByToken(verificationCode);
	   Passenger passenger = token.getPassenger();
	     
	    if (passenger == null || passenger.isEnabled()) {
	        return false;
	    } else {
	    	tokenRepository.deleteByPassenger(passenger);
	        passenger.setToken(null);
	        passenger.setEnabled(true);
	    	passenger.setJwt(jwtTokenUtil.generateToken(passenger.getId(), passenger.getEmail(), null));
	        passengerRepository.save(passenger);
	         
	        return true;
	    }
	}
	
	@Scheduled(fixedDelay = 1000 * 60 * 60)
	@Transactional
	public void deleteUnverified() {
		System.out.println("Num of users" + passengerRepository.findAll().size());
		passengerRepository.deleteByExpiredToken();
		System.out.println("Num of users" + passengerRepository.findAll().size());;
	}


}
