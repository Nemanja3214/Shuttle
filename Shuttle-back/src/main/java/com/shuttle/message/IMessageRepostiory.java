package com.shuttle.message;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IMessageRepostiory extends JpaRepository<Message, Long> {
	@Query("select m from Message m where m.sender.id = :id or m.receiver.id = :id")
	List<Message> findByUser(Long id);
}
