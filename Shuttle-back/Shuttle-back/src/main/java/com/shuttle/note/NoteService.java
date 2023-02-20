package com.shuttle.note;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.shuttle.user.GenericUser;

@Service
public class NoteService implements INoteService {
	@Autowired
	private INoteRepository noteRepo;

	@Override
	public Note save(Note n) {
		return noteRepo.save(n);
	}

	@Override
	public Note create(GenericUser user, GenericUser createdBy, String message) {
		Note n = new Note();
		n.setMessage(message);
		n.setUser(user);
		n.setBy(createdBy);
		n.setTimeCreated(LocalDateTime.now());
		n = noteRepo.save(n);
		return n;
	}

	@Override
	public List<Note> findAll(GenericUser user, Pageable pageable) {
		return noteRepo.findAll(user.getId(), pageable);
	}

}
