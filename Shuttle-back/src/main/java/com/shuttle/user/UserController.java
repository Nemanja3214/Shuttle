package com.shuttle.user;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

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
import com.shuttle.message.dto.SentMessageDTO;
import com.shuttle.note.dto.NoteDTO;
import com.shuttle.note.dto.ReadNoteDTO;
import com.shuttle.ride.dto.ReadRideDTO;
import com.shuttle.user.dto.ReadUserDTO;

@RestController
@RequestMapping("user")
public class UserController {

	@GetMapping
	@RequestMapping("{id}/ride")
	public ReadRideDTO getUserRides(@PathVariable long id, @PathVariable long page,
			@PathVariable long size, @PathVariable String sort, @PathVariable LocalDateTime from, @PathVariable LocalDateTime to) {
		return new ReadRideDTO();
	}
	
	@GetMapping
	public ReadUserDTO getUser(@PathVariable long page, @PathVariable long size) {
		return new ReadUserDTO();
	}
	
	@PostMapping("/login")
	public TokenDTO login(@RequestBody CredentialsDTO credentialsDTO) {
		return new TokenDTO();
	}
	
	@GetMapping("/{id}/message")
	public Collection<MessageDTO> getMessages(@PathVariable long userId){
		return new ArrayList<MessageDTO>();
	}
	
	@PostMapping("/{id}/message")
	public SentMessageDTO sendMessage(@RequestBody CreateMessageDTO messageDTO) {
		return new SentMessageDTO();
	}
	
	@PutMapping("/{id}/block")
	public boolean block(@PathVariable long userId){
		return true;
	}
	
	@PutMapping("{id}/unblock")
	public boolean unblock(@PathVariable long userId) {
		return true;
	}
	
	@PostMapping("{id}/note")
	public NoteDTO createNote(@RequestBody String message) {
		return new NoteDTO();
	}
	
	@GetMapping("{id}/note")
	public Collection<ReadNoteDTO> getUserNotes(@PathVariable long id, @PathVariable long page, @PathVariable long size){
		return new ArrayList<ReadNoteDTO>();
	}
}
