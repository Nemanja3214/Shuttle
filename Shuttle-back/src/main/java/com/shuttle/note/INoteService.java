package com.shuttle.note;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.shuttle.user.GenericUser;

public interface INoteService {
	Note save(Note n);
	Note create(GenericUser user, GenericUser createdBy, String message);
	List<Note> findAll(GenericUser user, Pageable pageable);
}
