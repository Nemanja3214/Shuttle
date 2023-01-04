package com.shuttle.message;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IMessageRepostiory extends JpaRepository<Message, Long> {
}
