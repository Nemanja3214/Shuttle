package com.shuttle.note;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface INoteRepository extends JpaRepository<Note, Long> {
	@Query("select n from Note n where n.user.id = :userId")
	List<Note> findAll(Long userId, Pageable pageable);
}
