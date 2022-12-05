package com.shuttle.note.dto;

import java.util.Collection;

public class ReadNotesDTO {
	private long totalCount;
	private Collection<NoteDTO> notes;
	
	public long getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}
	public Collection<NoteDTO> getNotes() {
		return notes;
	}
	public void setNotes(Collection<NoteDTO> notes) {
		this.notes = notes;
	}
	
	

}
