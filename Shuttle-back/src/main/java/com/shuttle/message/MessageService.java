package com.shuttle.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
