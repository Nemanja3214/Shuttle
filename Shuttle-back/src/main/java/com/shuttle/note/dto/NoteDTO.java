package com.shuttle.note.dto;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import com.shuttle.note.Note;
import com.shuttle.note.NoteMessage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoteDTO {
	private Long id;
	private String message;
	private String date;
	
	public NoteDTO(Note n) {
		this.id = n.getId();
		this.message = n.getMessage();
		this.date = n.getTimeCreated().toString();
	}
}
