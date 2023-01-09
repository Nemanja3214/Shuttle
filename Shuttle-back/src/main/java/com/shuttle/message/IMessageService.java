package com.shuttle.message;

import java.util.List;

import com.shuttle.user.GenericUser;

public interface IMessageService {
    public Message save(Message m);
    public Message findById(Long id);
	public List<Message> findByUser(GenericUser u);
}
