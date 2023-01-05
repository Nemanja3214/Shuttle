package com.shuttle.message;

public interface IMessageService {
    public Message save(Message m);
    public Message findById(Long id);
}
