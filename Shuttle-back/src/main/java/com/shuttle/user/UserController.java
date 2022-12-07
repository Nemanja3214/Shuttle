package com.shuttle.user;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shuttle.common.ListDTO;
import com.shuttle.credentials.dto.CredentialsDTO;
import com.shuttle.credentials.dto.TokenDTO;
import com.shuttle.message.dto.CreateMessageDTO;
import com.shuttle.message.dto.MessageDTO;
import com.shuttle.note.dto.NoteDTO;
import com.shuttle.user.dto.UserDTO;

import jakarta.websocket.server.PathParam;

@RestController
@RequestMapping("/api/user")
public class UserController {

	@GetMapping("/{id}/ride")
	public ResponseEntity<ListDTO<String>> getUserRides(
			@PathVariable long id,
			@PathParam("page") long page,
			@PathParam("size") long size,
			@PathParam("sort") String sort,
			@RequestParam @DateTimeFormat(pattern="HH:mm:ss dd.MM.yyyy")  LocalDateTime from,
			@PathVariable @DateTimeFormat(pattern="HH:mm:ss dd.MM.yyyy")  LocalDateTime to) {
		
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
		return new ResponseEntity<TokenDTO>(TokenDTO.getMock(), HttpStatus.OK);
	}
	
	@GetMapping("/{id}/message")
	public ResponseEntity<ListDTO<MessageDTO>> getMessages(@PathVariable long id){
		
		ListDTO<MessageDTO> messages = new ListDTO<>();
		messages.setTotalCount(243);
		messages.getResults().add(MessageDTO.getMock());
		
		return new ResponseEntity<>(messages, HttpStatus.OK);
	}
	
	@PostMapping("/{id}/message")
	public ResponseEntity<MessageDTO> sendMessage(@RequestBody CreateMessageDTO messageDTO) {
		return new ResponseEntity<MessageDTO>(MessageDTO.getMock(), HttpStatus.OK);
	}
	
	@PutMapping("/{id}/block")
	public ResponseEntity<Boolean> block(@PathVariable long id){
		return new ResponseEntity<Boolean>(true, HttpStatus.NO_CONTENT);
	}
	
	@PutMapping("/{id}/unblock")
	public ResponseEntity<Boolean> unblock(@PathVariable long id) {
		return new ResponseEntity<Boolean>(true, HttpStatus.NO_CONTENT);
	}
	
	@PostMapping("/{id}/note")
	public ResponseEntity<NoteDTO> createNote(@PathVariable long id, @RequestBody String message) {
		return new ResponseEntity<NoteDTO>(NoteDTO.getMock(), HttpStatus.OK);
	}
	
	@GetMapping("/{id}/note")
	public ResponseEntity<ListDTO<NoteDTO>> getUserNotes(
			@PathVariable long id,
			@RequestParam long page,
			@RequestParam long size){
		
		ListDTO<NoteDTO> notes = new ListDTO<>();
		notes.setTotalCount(243);
		notes.getResults().add(NoteDTO.getMock());
		
		return new ResponseEntity<>(notes, HttpStatus.OK);
	}
}
