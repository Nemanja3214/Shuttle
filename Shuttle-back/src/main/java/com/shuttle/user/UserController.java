package com.shuttle.user;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shuttle.common.ListDTO;
import com.shuttle.credentials.dto.CredentialsDTO;
import com.shuttle.credentials.dto.TokenDTO;
import com.shuttle.message.dto.CreateMessageDTO;
import com.shuttle.message.dto.MessageDTO;
import com.shuttle.note.dto.NoteDTO;
import com.shuttle.user.dto.UserDTO;

@RestController
@RequestMapping("/api/user")
public class UserController {

	@GetMapping
	@RequestMapping("/{id}/ride")
	public ResponseEntity<ListDTO<String>> getUserRides(@PathVariable long id, @PathVariable long page,
														@PathVariable long size, @PathVariable String sort, @PathVariable LocalDateTime from, @PathVariable LocalDateTime to) {
		return new ResponseEntity<ListDTO<String>>(new ListDTO<String>(), HttpStatus.OK);
	}
	
	@GetMapping
	public ResponseEntity<ListDTO<UserDTO>> getUser(@PathVariable long page, @PathVariable long size) {
		return new ResponseEntity<ListDTO<UserDTO>>(new ListDTO<UserDTO>(), HttpStatus.OK);
	}
	
	@PostMapping("/login")
	public ResponseEntity<TokenDTO> login(@RequestBody CredentialsDTO credentialsDTO) {
		return new ResponseEntity<TokenDTO>(new TokenDTO(), HttpStatus.OK);
	}
	
	@GetMapping("/{id}/message")
	public ResponseEntity<ListDTO<MessageDTO>> getMessages(@PathVariable long userId){
		return new ResponseEntity<ListDTO<MessageDTO>>(new ListDTO<MessageDTO>(), HttpStatus.OK);
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
	public ResponseEntity<NoteDTO> createNote(@PathVariable long userId, @RequestBody String message) {
		return new ResponseEntity<NoteDTO>(new NoteDTO(), HttpStatus.OK);
	}
	
	@GetMapping("/{id}/note")
	public ResponseEntity<ListDTO<NoteDTO>> getUserNotes(@PathVariable long id, @PathVariable long page, @PathVariable long size){
		return new ResponseEntity<ListDTO<NoteDTO>>(new ListDTO<NoteDTO>(), HttpStatus.OK);
	}
}
