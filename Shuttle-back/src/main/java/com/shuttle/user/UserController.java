package com.shuttle.user;


import java.io.IOException;
import com.shuttle.security.jwt.JwtTokenUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

import org.aspectj.apache.bcel.classfile.ExceptionTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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
import com.shuttle.message.dto.CreateMessageDTO;
import com.shuttle.message.dto.MessageDTO;
import com.shuttle.note.dto.NoteDTO;
import com.shuttle.security.jwt.JwtTokenUtil;
import com.shuttle.ride.IRideService;
import com.shuttle.ride.Ride;
import com.shuttle.ride.cancellation.Cancellation;
import com.shuttle.ride.dto.RideDTO;
import com.shuttle.user.dto.PasswordDTO;
import com.shuttle.user.dto.UserDTO;
import com.shuttle.user.passwordReset.IPasswordResetService;
import com.shuttle.user.passwordReset.PasswordResetCode;
import com.shuttle.user.passwordReset.dto.PasswordResetCodeDTO;
import com.shuttle.vehicle.Vehicle;

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
    
    
    @PreAuthorize("hasAnyAuthority('admin', 'passenger', 'driver')")
    @PutMapping("/{id}/changePassword")
    public ResponseEntity<?> changePassword(@PathVariable Long id, @RequestBody PasswordDTO passwordDTO) {
    	if (id == null) {
			return new ResponseEntity<RESTError>(new RESTError("Field id is required!"), HttpStatus.BAD_REQUEST);
		}
    	
    	//if (Pattern.matches("^(?=.*\\d)(?=.*[A-Z])(?!.*[^a-zA-Z0-9@#$^+=])(.{8,15})$", passwordDTO.getNew_password())) {
    	//	return new ResponseEntity<RESTError>(new RESTError("Field (new_password) format is not valid!!"), HttpStatus.BAD_REQUEST);
    	//}
    	
    	//if (Pattern.matches("^(?=.*\\d)(?=.*[A-Z])(?!.*[^a-zA-Z0-9@#$^+=])(.{8,15})$", passwordDTO.getNew_password())) {
    	//	return new ResponseEntity<RESTError>(new RESTError("Field (old_password) format is not valid!!"), HttpStatus.BAD_REQUEST);
    	//}
		
		GenericUser u = userService.findById(id);	
		if (u == null) {
			return new ResponseEntity<RESTError>(new RESTError("User does not exist!"), HttpStatus.NOT_FOUND);
		}
		
		final GenericUser user____ = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		if (userService.isAdmin(user____)) {	
		} else {
	    	if (!u.getId().equals(user____.getId())) {
                return new ResponseEntity<RESTError>(new RESTError("User does not exist!"), HttpStatus.NOT_FOUND);
	    	}
	    }
		
		if (!userService.hasPassword(u, passwordDTO.getOld_password())) {
			return new ResponseEntity<RESTError>(new RESTError("Current password is not matching!"), HttpStatus.BAD_REQUEST);
		}
		userService.changePassword(u, passwordDTO.getNew_password());
		
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }
    
    @PreAuthorize("hasAnyAuthority('admin', 'passenger', 'driver')")
    @GetMapping("/{id}/resetPassword")
    public ResponseEntity<?> resetPasswordSendEmail(@PathVariable Long id) {
    	if (id == null) {
			return new ResponseEntity<RESTError>(new RESTError("Field id is required!"), HttpStatus.BAD_REQUEST);
		}
		
		GenericUser u = userService.findById(id);	
		if (u == null) {
			return new ResponseEntity<RESTError>(new RESTError("User does not exist!"), HttpStatus.NOT_FOUND);
		}
		
		final GenericUser user____ = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		if (userService.isAdmin(user____)) {	
		} else {
	    	if (!u.getId().equals(user____.getId())) {
                return new ResponseEntity<RESTError>(new RESTError("User does not exist!"), HttpStatus.NOT_FOUND);
	    	}
	    }
		
		if (passwordResetService.findByUser(u) != null) {
			return new ResponseEntity<RESTError>(new RESTError("Active code already exists!"), HttpStatus.BAD_REQUEST);
		}
		
		PasswordResetCode prc = passwordResetService.create(u);
		System.out.println("Sending email to " + u.getEmail() + " with code " + prc.getCode() + ". There's a time limit.");
		
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT); 
    }
    
    @PreAuthorize("hasAnyAuthority('admin', 'passenger', 'driver')")
    @PutMapping("/{id}/resetPassword")
    public ResponseEntity<?> resetPassword(@PathVariable Long id, @RequestBody PasswordResetCodeDTO dto) {
    	if (id == null) {
			return new ResponseEntity<RESTError>(new RESTError("Field id is required!"), HttpStatus.BAD_REQUEST);
		}
    	
    	//if (!Pattern.matches("^(?=.*\\d)(?=.*[A-Z])(?!.*[^a-zA-Z0-9@#$^+=])(.{8,15})$", dto.getNew_password())) {
    	//	return new ResponseEntity<RESTError>(new RESTError("Field (new_password) format is not valid!!"), HttpStatus.BAD_REQUEST);
    	//}
    	
    	if (!Pattern.matches("^[0-9]{1,6}$", dto.getCode())) {
    		return new ResponseEntity<RESTError>(new RESTError("Field (code) format is not valid!!"), HttpStatus.BAD_REQUEST);
    	}
		
		GenericUser u = userService.findById(id);	
		if (u == null) {
			return new ResponseEntity<RESTError>(new RESTError("User does not exist!"), HttpStatus.NOT_FOUND);
		}
		
		final GenericUser user____ = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		if (userService.isAdmin(user____)) {	
		} else {
	    	if (!u.getId().equals(user____.getId())) {
                return new ResponseEntity<RESTError>(new RESTError("User does not exist!"), HttpStatus.NOT_FOUND);
	    	}
	    }
		
		List<PasswordResetCode> requests = passwordResetService.findByUserMaybeExpired(u);
		PasswordResetCode req = requests.stream().filter(r -> r.getCode().equals(dto.getCode())).findFirst().orElse(null);

		if (req == null || !req.getActive() || req.getExpires().isBefore(LocalDateTime.now())) {
			return new ResponseEntity<RESTError>(new RESTError("Code is expired or not correct!"), HttpStatus.NOT_FOUND);
		}
		
		for (PasswordResetCode pr : requests) {
			passwordResetService.invalidate(pr);
		}
		
		userService.changePassword(u, dto.getNew_password());
		
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }
    

    @GetMapping("/{id}/ride")
    public ResponseEntity<ListDTO<String>> getUserRides(
            @PathVariable long id,
            @PathParam("page") long page,
            @PathParam("size") long size,
            @PathParam("sort") String sort,
            @RequestParam String from,
            @RequestParam String to) {

        ListDTO<String> rides = new ListDTO<>();
        rides.setTotalCount(243);
        rides.getResults().add("string");

        return new ResponseEntity<>(rides, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ListDTO<UserDTO>> getUsers(
            @RequestParam Long page,
            @RequestParam Long size) {

        ListDTO<UserDTO> users = new ListDTO<>();
        users.setTotalCount(243);
        users.getResults().add(UserDTO.getMock());

        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody CredentialsDTO credentialsDTO) {
        UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(credentialsDTO.getEmail(), credentialsDTO.getPassword());
        Authentication auth = authenticationManager.authenticate(authReq);
        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(auth);
        String token = jwtTokenUtil.generateToken
                (((GenericUser) auth.getPrincipal()).getId(), credentialsDTO.getEmail(), auth.getAuthorities());
        TokenDTO tokens = new TokenDTO(token, token);
        //TODO add refresh token
        
        GenericUser user = userService.findByEmail(credentialsDTO.getEmail());
        if (user != null) {
        	userService.setActive(user, true);
        }
        
        
        return new ResponseEntity<TokenDTO>(tokens, HttpStatus.OK);
    }

    @GetMapping("/{id}/message")
    public ResponseEntity<ListDTO<MessageDTO>> getMessages(@PathVariable long id) {

        ListDTO<MessageDTO> messages = new ListDTO<>();
        messages.setTotalCount(243);
        messages.getResults().add(new MessageDTO());

        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    @PostMapping("/{recieverId}/message")
    public ResponseEntity<?> sendMessage(@PathVariable Long recieverId, @RequestBody CreateMessageDTO messageDTO) {
        if (recieverId == null) {
			return new ResponseEntity<RESTError>(new RESTError("Bad ID format."), HttpStatus.BAD_REQUEST);
        }

        GenericUser sender = null;
        
        try {
            sender = (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        } catch (Exception e) {}
        
        GenericUser reciever = null;
        if (recieverId == -1) {
            final List<GenericUser> admins = userService.findByRole("admin");
            if (admins.size() != 0) {
                reciever = admins.get(0);
            }
        } else {
            reciever = userService.findById(recieverId);
        }

        Ride ride = rideService.findById(messageDTO.getRideId());

		if (sender == null || reciever == null || ride == null) {
			return new ResponseEntity<Void>((Void)null, HttpStatus.NOT_FOUND);
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

    @PutMapping("/{id}/block")
    public ResponseEntity<Boolean> block(@PathVariable long id) {
        return new ResponseEntity<Boolean>(true, HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}/unblock")
    public ResponseEntity<Boolean> unblock(@PathVariable long id) {
        return new ResponseEntity<Boolean>(true, HttpStatus.NO_CONTENT);
    }
    
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

    @PostMapping("/{id}/note")
    public ResponseEntity<NoteDTO> createNote(@PathVariable long id, @RequestBody String message) {
        return new ResponseEntity<NoteDTO>(NoteDTO.getMock(), HttpStatus.OK);
    }

    @GetMapping("/{id}/note")
    public ResponseEntity<ListDTO<NoteDTO>> getUserNotes(
            @PathVariable long id,
            @RequestParam long page,
            @RequestParam long size) {

        ListDTO<NoteDTO> notes = new ListDTO<>();
        notes.setTotalCount(243);
        notes.getResults().add(NoteDTO.getMock());

        return new ResponseEntity<>(notes, HttpStatus.OK);
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
}
