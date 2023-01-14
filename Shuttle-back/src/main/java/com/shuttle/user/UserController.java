package com.shuttle.user;


import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.shuttle.security.jwt.JwtTokenUtil;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Pattern;

import org.aspectj.apache.bcel.classfile.ExceptionTable;
import jakarta.annotation.security.PermitAll;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shuttle.common.ListDTO;
import com.shuttle.common.RESTError;
import com.shuttle.common.exception.NonExistantUserException;
import com.shuttle.credentials.dto.CredentialsDTO;
import com.shuttle.credentials.dto.TokenDTO;
import com.shuttle.message.IMessageService;
import com.shuttle.message.Message;
import com.shuttle.message.Message.Type;
import com.shuttle.message.dto.CreateMessageDTO;
import com.shuttle.message.dto.MessageDTO;
import com.shuttle.note.INoteService;
import com.shuttle.note.Note;
import com.shuttle.note.NoteMessage;
import com.shuttle.note.dto.NoteDTO;
import com.shuttle.passenger.Passenger;
import com.shuttle.security.jwt.JwtTokenUtil;
import com.shuttle.ride.IRideService;
import com.shuttle.ride.Ride;
import com.shuttle.ride.RideController;
import com.shuttle.ride.cancellation.Cancellation;
import com.shuttle.ride.dto.RideDTO;
import com.shuttle.ride.dto.RidePassengerDTO;
import com.shuttle.user.dto.BasicUserInfoDTO;
import com.shuttle.user.dto.PasswordDTO;
import com.shuttle.user.dto.UserDTO;
import com.shuttle.user.dto.UserDTONoPassword;
import com.shuttle.user.email.IEmailService;
import com.shuttle.user.passwordReset.IPasswordResetService;
import com.shuttle.user.passwordReset.PasswordResetCode;
import com.shuttle.user.passwordReset.dto.PasswordResetCodeDTO;
import com.shuttle.util.MyValidator;
import com.shuttle.util.MyValidatorException;
import com.shuttle.vehicle.Vehicle;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.websocket.server.PathParam;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private UserService userService;
    @Autowired
    private IMessageService messageService;
    @Autowired
    private IRideService rideService;
    @Autowired
    private IPasswordResetService passwordResetService;
    @Autowired
    private INoteService noteService;
    @Autowired
    private IEmailService emailService;
    
    @PreAuthorize("hasAnyAuthority('admin', 'passenger', 'driver')")
    @PutMapping("/{id}/changePassword")
    public ResponseEntity<?> changePassword(@PathVariable Long id, @RequestBody PasswordDTO passwordDTO) {
    	try {
			MyValidator.validateRequired(passwordDTO.getNewPassword(), "newPassword");
			MyValidator.validateRequired(passwordDTO.getOldPassword(), "oldPassword");
			
			MyValidator.validatePattern(passwordDTO.getNewPassword(), "newPassword", "^(?=.*\\d)(?=.*[A-Z])(?!.*[^a-zA-Z0-9@#$^+=])(.{8,15})$");
			MyValidator.validatePattern(passwordDTO.getOldPassword(), "oldPassword", "^(?=.*\\d)(?=.*[A-Z])(?!.*[^a-zA-Z0-9@#$^+=])(.{8,15})$");
		} catch (MyValidatorException e1) {
			return new ResponseEntity<RESTError>(new RESTError(e1.getMessage()), HttpStatus.BAD_REQUEST);
		}
    	
    	if (id == null) {
			return new ResponseEntity<RESTError>(new RESTError("Field id is required!"), HttpStatus.BAD_REQUEST);
		}
		
		GenericUser u = userService.findById(id);	
		if (u == null) {
			return new ResponseEntity<>("User does not exist!", HttpStatus.NOT_FOUND);
		}
		
		final GenericUser user____ = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		if (userService.isAdmin(user____)) {	
		} else {
	    	if (!u.getId().equals(user____.getId())) {
                return new ResponseEntity<>("User does not exist!", HttpStatus.NOT_FOUND);
	    	}
	    }
		
		if (!userService.hasPassword(u, passwordDTO.getOldPassword())) {
			return new ResponseEntity<RESTError>(new RESTError("Current password is not matching!"), HttpStatus.BAD_REQUEST);
		}
		userService.changePassword(u, passwordDTO.getNewPassword());
		
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }
    
    //@PreAuthorize("hasAnyAuthority('admin', 'passenger', 'driver')")
    @GetMapping("/{id}/resetPassword")
    public ResponseEntity<?> resetPasswordSendEmail(@PathVariable Long id) {
    	if (id == null) {
			return new ResponseEntity<RESTError>(new RESTError("Field id is required!"), HttpStatus.BAD_REQUEST);
		}
		
		GenericUser u = userService.findById(id);	
		if (u == null) {
			return new ResponseEntity<>("User does not exist!", HttpStatus.NOT_FOUND);
		}
		
//		final GenericUser user____ = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
//		if (user____ != null) {
//			if (userService.isAdmin(user____)) {	
//			} else {
//		    	if (!u.getId().equals(user____.getId())) {
//	                return new ResponseEntity<>("User does not exist!", HttpStatus.NOT_FOUND);
//		    	}
//		    }
//		}
		
		if (passwordResetService.findByUser(u) != null) {
			return new ResponseEntity<RESTError>(new RESTError("Active code already exists!"), HttpStatus.BAD_REQUEST);
		}
		
		PasswordResetCode prc = passwordResetService.create(u);
		
		try {
			System.out.println("Sending email to " + u.getEmail() + " with code " + prc.getCode() + ". There's a time limit.");
			emailService.sendPasswordResetEmail(prc);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT); 
    }
    
    //@PreAuthorize("hasAnyAuthority('admin', 'passenger', 'driver')")
    @PutMapping("/{id}/resetPassword")
    public ResponseEntity<?> resetPassword(@PathVariable Long id, @RequestBody PasswordResetCodeDTO dto) {
    	try {
			MyValidator.validateRequired(dto.getNew_password(), "new_password");
			MyValidator.validateRequired(dto.getCode(), "code");
			
			MyValidator.validatePattern(dto.getNew_password(), "new_password", "^(?=.*\\d)(?=.*[A-Z])(?!.*[^a-zA-Z0-9@#$^+=])(.{8,15})$");
			MyValidator.validatePattern(dto.getCode(), "code", "^[0-9]{1,6}$");
		} catch (MyValidatorException e1) {
			return new ResponseEntity<RESTError>(new RESTError(e1.getMessage()), HttpStatus.BAD_REQUEST);
		}
    	
    	if (id == null) {
			return new ResponseEntity<RESTError>(new RESTError("Field id is required!"), HttpStatus.BAD_REQUEST);
		}
    	
		GenericUser u = userService.findById(id);	
		if (u == null) {
			return new ResponseEntity<>("User does not exist!", HttpStatus.NOT_FOUND);
		}
		
//		final GenericUser user____ = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
//		if (userService.isAdmin(user____)) {	
//		} else {
//	    	if (!u.getId().equals(user____.getId())) {
//                return new ResponseEntity<>("User does not exist!", HttpStatus.NOT_FOUND);
//	    	}
//	    }
		
		List<PasswordResetCode> requests = passwordResetService.findByUserMaybeExpired(u);
		PasswordResetCode req = requests.stream().filter(r -> r.getCode().equals(dto.getCode())).findFirst().orElse(null);

		if (req == null || !req.getActive() || req.getExpires().isBefore(LocalDateTime.now())) {
			return new ResponseEntity<>("Code is expired or not correct!", HttpStatus.NOT_FOUND);
		}
		
		for (PasswordResetCode pr : requests) {
			passwordResetService.invalidate(pr);
		}
		
		userService.changePassword(u, dto.getNew_password());
		
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }
    
    @PreAuthorize("hasAnyAuthority('admin', 'passenger', 'driver')")
    @GetMapping("/{id}/ride")
    public ResponseEntity<?> getUserRides(@PathVariable Long id, Pageable pageable, @RequestParam(required = false) String from, @RequestParam(required = false) String to) {
    	if (id == null) {
			return new ResponseEntity<RESTError>(new RESTError("Field id is required!"), HttpStatus.BAD_REQUEST);
		}
    	
    	LocalDateTime tFrom = null, tTo = null;
    	if (from != null && to != null) {
    		try {
    			DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.of("UTC"));
    			tFrom = LocalDateTime.parse(from, formatter);
    			//tFrom = LocalDateTime.from(DateTimeFormatter.ISO_INSTANT.parse(from));
    		} catch (DateTimeParseException e) {
    			return new ResponseEntity<RESTError>(new RESTError("Field (from) format is not valid!"), HttpStatus.BAD_REQUEST);
    		}
    		try {
    			DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.of("UTC"));
    			tTo = LocalDateTime.parse(to, formatter);
    			//tTo = LocalDateTime.from(DateTimeFormatter.ISO_INSTANT.parse(to));
    		} catch (DateTimeParseException e) {
    			return new ResponseEntity<RESTError>(new RESTError("Field (to) format is not valid!"), HttpStatus.BAD_REQUEST);
    		}
    	}
    	
    	GenericUser u = userService.findById(id);	
		if (u == null) {
			return new ResponseEntity<>("User does not exist!", HttpStatus.NOT_FOUND);
		}
		
		final GenericUser user____ = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		if (userService.isAdmin(user____)) {	
		} else {
	    	if (!u.getId().equals(user____.getId())) {
                return new ResponseEntity<>("User does not exist!", HttpStatus.NOT_FOUND);
	    	}
	    }
		
		List<Ride> rides = rideService.findByUser(u, pageable, tFrom, tTo);
		ListDTO<RideDTO> ridesDTO = new ListDTO<>(rides.stream().map(r -> RideController.to(r)).toList());
        return new ResponseEntity<>(ridesDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('admin')")
    @GetMapping
    public ResponseEntity<?> getUsers(Pageable pageable) {
		final GenericUser user____ = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		if (userService.isAdmin(user____)) {	
		}
		
		List<GenericUser> users = userService.findAll(pageable);
		ListDTO<UserDTONoPassword> usersDTO = new ListDTO<>(users.stream().map(u -> new UserDTONoPassword(u)).toList());
        return new ResponseEntity<>(usersDTO, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody CredentialsDTO credentialsDTO) {
    	try {
			MyValidator.validateRequired(credentialsDTO.getEmail(), "email");
			MyValidator.validateRequired(credentialsDTO.getPassword(), "password");
			
			MyValidator.validateEmail(credentialsDTO.getEmail(), "email");
		} catch (MyValidatorException e1) {
			return new ResponseEntity<RESTError>(new RESTError(e1.getMessage()), HttpStatus.BAD_REQUEST);
		}
    	
    	UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(credentialsDTO.getEmail(), credentialsDTO.getPassword());
        
        Authentication auth = null;
        try {
        	auth = authenticationManager.authenticate(authReq);
        } catch (BadCredentialsException e) {
        	return new ResponseEntity<RESTError>(new RESTError("Wrong username or password!"), HttpStatus.BAD_REQUEST);
        }
        
        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(auth);
        long id = ((GenericUser) auth.getPrincipal()).getId();
        String token = jwtTokenUtil.generateToken(id, credentialsDTO.getEmail(), auth.getAuthorities());
        String refreshToken = jwtTokenUtil.generateRefreshToken(id, credentialsDTO.getEmail());
        TokenDTO tokens = new TokenDTO(token, refreshToken);

        GenericUser user = userService.findByEmail(credentialsDTO.getEmail());
        if (user != null) {
            userService.setActive(user, true);
        }
        
        return new ResponseEntity<TokenDTO>(tokens, HttpStatus.OK);
    }


    @PermitAll
    @PostMapping(value = "/refreshtoken")
    public ResponseEntity<TokenDTO> refreshtoken(@RequestBody String refreshToken) throws Exception {
        // From the HttpRequest get the claims
        refreshToken = refreshToken.replace("\"","");
        refreshToken = refreshToken.replace("{","");
        refreshToken = refreshToken.replace("}","");
        refreshToken = refreshToken.substring(13).strip();
        refreshToken = refreshToken.replace("\\","");
        String email = jwtTokenUtil.getEmailFromToken(refreshToken);
        GenericUser user = userService.findByEmail(email);

        if (jwtTokenUtil.validateToken(refreshToken, user)) {
            String token = jwtTokenUtil.generateToken(user.getId(), user.getEmail(), user.getAuthorities());
            System.out.println("Refreshed token");
            TokenDTO tokenDTO = new TokenDTO(token,refreshToken);
            return new ResponseEntity<>(tokenDTO, HttpStatus.OK);

        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PreAuthorize("hasAnyAuthority('admin', 'passenger', 'driver')")
    @GetMapping("/{id}/message")
    public ResponseEntity<?> getMessages(@PathVariable Long id) {
    	if (id == null) {
			return new ResponseEntity<RESTError>(new RESTError("Field id is required!"), HttpStatus.BAD_REQUEST);
		}
		
		GenericUser u = userService.findById(id);	
		if (u == null) {
			return new ResponseEntity<>("User does not exist!", HttpStatus.NOT_FOUND);
		}
		
		final GenericUser user____ = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		if (userService.isAdmin(user____)) {	
		} else {
	    	if (!u.getId().equals(user____.getId())) {
                return new ResponseEntity<>("User does not exist!", HttpStatus.NOT_FOUND);
	    	}
	    }
		
    	List<Message> messages = messageService.findByUser(u);
        ListDTO<MessageDTO> messagesDTO = new ListDTO<>(messages.stream().map(m -> new MessageDTO(m)).toList());
        return new ResponseEntity<>(messagesDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('admin', 'passenger', 'driver')")
    @PostMapping("/{recieverId}/message")
    public ResponseEntity<?> sendMessage(@PathVariable Long recieverId, @RequestBody CreateMessageDTO messageDTO) {
    	try {
			MyValidator.validateRequired(messageDTO.getMessage(), "message");
			MyValidator.validateRequired(messageDTO.getType(), "type");
			MyValidator.validateRequired(messageDTO.getRideId(), "rideId");
				
			MyValidator.validateLength(messageDTO.getMessage(), "email", 500);
		} catch (MyValidatorException e1) {
			return new ResponseEntity<RESTError>(new RESTError(e1.getMessage()), HttpStatus.BAD_REQUEST);
		}
    	
    	if (recieverId == null) {
			return new ResponseEntity<RESTError>(new RESTError("Bad ID format."), HttpStatus.BAD_REQUEST);
        }

        GenericUser sender = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        
        if (sender == null) {
        	return new ResponseEntity<>("User does not exist.", HttpStatus.NOT_FOUND);
        }
        
        GenericUser reciever = null;
        if (recieverId == -1) {
            final List<GenericUser> admins = userService.findByRole("admin");
            if (admins.size() != 0) {
                reciever = admins.get(0);
            }
        } else {
            reciever = userService.findById(recieverId);
        }
        
        if (reciever == null) {
        	return new ResponseEntity<>("Receiver does not exist.", HttpStatus.NOT_FOUND);
        }

        Ride ride = rideService.findById(messageDTO.getRideId());

		if (ride == null && messageDTO.getType() != Type.SUPPORT) {
			return new ResponseEntity<>("Ride does not exist.", HttpStatus.NOT_FOUND);
		}

        Message m = new Message(
            null,
            sender,
            reciever,
            messageDTO.getMessage(),
            LocalDateTime.now(),
            ride,
            messageDTO.getType()
        );
        m = messageService.save(m);

        return new ResponseEntity<MessageDTO>(new MessageDTO(m), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('admin')")
    @PutMapping("/{id}/block")
    public ResponseEntity<?> block(@PathVariable Long id) {
    	if (id == null) {
			return new ResponseEntity<RESTError>(new RESTError("Field id is required!"), HttpStatus.BAD_REQUEST);
		}
		
		GenericUser u = userService.findById(id);	
		if (u == null) {
			return new ResponseEntity<>("User does not exist!", HttpStatus.NOT_FOUND);
		}
		
		if (u.getBlocked()) {
			return new ResponseEntity<>(new RESTError("User is already blocked!"), HttpStatus.BAD_REQUEST);	
		}
		
		u = userService.setBlocked(u, true);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAnyAuthority('admin')")
    @PutMapping("/{id}/unblock")
    public ResponseEntity<?> unblock(@PathVariable Long id) {
    	if (id == null) {
			return new ResponseEntity<RESTError>(new RESTError("Field id is required!"), HttpStatus.BAD_REQUEST);
		}
		
		GenericUser u = userService.findById(id);	
		if (u == null) {
			return new ResponseEntity<>("User does not exist!", HttpStatus.NOT_FOUND);
		}
		
		if (!u.getBlocked()) {
			return new ResponseEntity<RESTError>(new RESTError("User is not blocked!"), HttpStatus.BAD_REQUEST);	
		}
		
		u = userService.setBlocked(u, false);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAnyAuthority('admin')")
    @PostMapping("/{id}/note")
    public ResponseEntity<?> createNote(@PathVariable Long id, @RequestBody NoteMessage message) {
    	try {
			MyValidator.validateRequired(message.getMessage(), "message");
			
			MyValidator.validateLength(message.getMessage(), "email", 500);
		} catch (MyValidatorException e1) {
			return new ResponseEntity<RESTError>(new RESTError(e1.getMessage()), HttpStatus.BAD_REQUEST);
		}
    	
    	if (id == null) {
			return new ResponseEntity<RESTError>(new RESTError("Field id is required!"), HttpStatus.BAD_REQUEST);
		}
		
		GenericUser u = userService.findById(id);	
		if (u == null) {
			return new ResponseEntity<>("User does not exist!", HttpStatus.NOT_FOUND);
		}
		
		final GenericUser creator = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		Note n = noteService.create(u, creator, message.getMessage());
	
        return new ResponseEntity<NoteDTO>(new NoteDTO(n), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('admin')")
    @GetMapping("/{id}/note")
    public ResponseEntity<?> getUserNotes(@PathVariable Long id, Pageable pageable) {
    	if (id == null) {
			return new ResponseEntity<RESTError>(new RESTError("Field id is required!"), HttpStatus.BAD_REQUEST);
		}
		
		GenericUser u = userService.findById(id);	
		if (u == null) {
			return new ResponseEntity<>("User does not exist!", HttpStatus.NOT_FOUND);
		}
		
		List<Note> notes = noteService.findAll(u, pageable);
		ListDTO<NoteDTO> notesDTO = new ListDTO<>(notes.stream().map(n -> new NoteDTO(n)).toList());		
        return new ResponseEntity<>(notesDTO, HttpStatus.OK);
    }
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    
    
    @GetMapping("/{id}/active")
    public ResponseEntity<?> getActive(@PathVariable Long id) {
		if (id == null) {
			return new ResponseEntity<RESTError>(new RESTError("Bad ID format."), HttpStatus.BAD_REQUEST);
		}	
		GenericUser user = userService.findById(id);
		if (user == null) {
			return new ResponseEntity<Void>((Void)null, HttpStatus.NOT_FOUND);
		}
		boolean isActive = userService.getActive(user);
		return new ResponseEntity<Boolean>(isActive, HttpStatus.OK);
    }
    
    @PutMapping("/{id}/active")
    public ResponseEntity<?> active(@PathVariable Long id) {
		if (id == null) {
			return new ResponseEntity<RESTError>(new RESTError("Bad ID format."), HttpStatus.BAD_REQUEST);
		}	
		GenericUser user = userService.findById(id);
		if (user == null) {
			return new ResponseEntity<Void>((Void)null, HttpStatus.NOT_FOUND);
		}
		user = userService.setActive(user, true);
		return new ResponseEntity<Boolean>(user.getActive(), HttpStatus.OK);
    }

    @PutMapping("/{id}/inactive")
    public ResponseEntity<?> inactive(@PathVariable Long id) {
		if (id == null) {
			return new ResponseEntity<RESTError>(new RESTError("Bad ID format."), HttpStatus.BAD_REQUEST);
		}	
		GenericUser user = userService.findById(id);
		if (user == null) {
			return new ResponseEntity<Void>((Void)null, HttpStatus.NOT_FOUND);
		}
		user = userService.setActive(user, false);
		return new ResponseEntity<Boolean>(user.getActive(), HttpStatus.OK);
    }
    
	@GetMapping("/img/{id}")
	public ResponseEntity<?> getProfilePicture(@PathVariable int id){
		try {
			String picture = this.userService.getProfilePicture(id);
			return new ResponseEntity<String>(picture, HttpStatus.OK);
		} catch (NonExistantUserException e) {
			return ResponseEntity.badRequest().body("User picture cannot be found");
		} catch (IOException e) {
			return ResponseEntity.internalServerError().body("Error saving picture");
		}
	}
	
    @GetMapping("/email")
    public ResponseEntity<?> getByEmail(@PathParam("email") String email) {
        GenericUser p = userService.findByEmail(email);
        if (p == null) {
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        } else {
        	BasicUserInfoDTO dto = new BasicUserInfoDTO(p);
            return new ResponseEntity<BasicUserInfoDTO>(dto, HttpStatus.OK);
        }
    }
}
