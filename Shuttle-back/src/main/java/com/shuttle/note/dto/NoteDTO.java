package com.shuttle.note.dto;

import java.time.ZonedDateTime;

public class NoteDTO {

	private long id;
	private ZonedDateTime date;
	private String message;
	
	
	
	public NoteDTO() {
		super();
	}

	public NoteDTO(long id, ZonedDateTime date, String message) {
		super();
		this.id = id;
		this.date = date;
		this.message = message;
	}
	
	public static NoteDTO getMock() {
		return new NoteDTO(123, ZonedDateTime.now(), "The passenger has requested and after that aborted the ride");
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public ZonedDateTime getDate() {
		return date;
	}
	public void setDate(ZonedDateTime date) {
		this.date = date;
	}
	
	
	
}
