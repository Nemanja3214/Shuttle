package com.shuttle.message;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Service;

import com.shuttle.user.GenericUser;

@Service
public class MessageService implements IMessageService {
    @Autowired
    private IMessageRepostiory messageRepostiory;

    @Override
    public Message save(Message m) {
        return messageRepostiory.save(m);
    }

    @Override
    public Message findById(Long id) {
        return messageRepostiory.findById(id).orElse(null);
    }

	@Override
	public List<Message> findByUser(GenericUser u) {
		return messageRepostiory.findByUser(u.getId());
	}
}
