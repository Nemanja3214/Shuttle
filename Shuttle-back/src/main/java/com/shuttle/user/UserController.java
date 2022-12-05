package com.shuttle.user;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shuttle.credentials.dto.CredentialsDTO;
import com.shuttle.credentials.dto.TokenDTO;
import com.shuttle.message.dto.CreateMessageDTO;
import com.shuttle.message.dto.MessageDTO;
import com.shuttle.message.dto.MessagesDTO;
import com.shuttle.note.dto.NoteDTO;
import com.shuttle.note.dto.ReadNotesDTO;
import com.shuttle.ride.dto.BaseRideDTO;
import com.shuttle.user.dto.UserDTO;

@RestController
@RequestMapping("/api/user")
public class UserController {

	@GetMapping
	@RequestMapping("/{id}/ride")
	public ResponseEntity<BaseRideDTO> getUserRides(@PathVariable long id, @PathVariable long page,
			@PathVariable long size, @PathVariable String sort, @PathVariable LocalDateTime from, @PathVariable LocalDateTime to) {
		return new ResponseEntity<BaseRideDTO>(new BaseRideDTO(), HttpStatus.OK);
	}
	
	@GetMapping
	public ResponseEntity<UserDTO> getUser(@PathVariable long page, @PathVariable long size) {
		return new ResponseEntity<UserDTO>(new UserDTO(), HttpStatus.OK);
	}
	
	@PostMapping("/login")
	public ResponseEntity<TokenDTO> login(@RequestBody CredentialsDTO credentialsDTO) {
		return new ResponseEntity<TokenDTO>(new TokenDTO(), HttpStatus.OK);
	}
	
	@GetMapping("/{id}/message")
	public ResponseEntity<Collection<MessagesDTO>> getMessages(@PathVariable long userId){
		return new ResponseEntity<Collection<MessagesDTO>>(new ArrayList<MessagesDTO>(), HttpStatus.OK);
	}
	
	@PostMapping("/{id}/message")
	public ResponseEntity<MessageDTO> sendMessage(@RequestBody CreateMessageDTO messageDTO) {
		return new ResponseEntity<MessageDTO>(new MessageDTO(), HttpStatus.OK);
	}
	
	@PutMapping("/{id}/block")
	public ResponseEntity<Boolean> block(@PathVariable long userId){
		return new ResponseEntity<Boolean>(true, HttpStatus.NO_CONTENT);
	}
	
	@PutMapping("/{id}/unblock")
	public ResponseEntity<Boolean> unblock(@PathVariable long userId) {
		return new ResponseEntity<Boolean>(true, HttpStatus.NO_CONTENT);
	}
	
	@PostMapping("/{id}/note")
	public ResponseEntity<NoteDTO> createNote(@RequestBody String message) {
		return new ResponseEntity<NoteDTO>(new NoteDTO(), HttpStatus.OK);
	}
	
	@GetMapping("/{id}/note")
	public ResponseEntity<ReadNotesDTO> getUserNotes(@PathVariable long id, @PathVariable long page, @PathVariable long size){
		return new ResponseEntity<ReadNotesDTO>(new ReadNotesDTO(), HttpStatus.OK);
	}
}
