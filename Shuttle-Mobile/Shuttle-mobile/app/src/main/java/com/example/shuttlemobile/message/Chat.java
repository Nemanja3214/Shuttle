package com.example.shuttlemobile.message;

import com.example.shuttlemobile.ride.Ride;
import com.example.shuttlemobile.user.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Chat implements Serializable {
    private User sender;
    private User recipient;
    private Ride ride;
    private List<MessageDTO> messages = new ArrayList<>();

    /**
     * @return Last message in this chat or <code>null</code> if none.
     */
    public MessageDTO getLastMessage() {
        if (messages == null)
            return null;
        if (messages.size() == 0)
            return null;
        return messages.get(messages.size() - 1);
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public List<MessageDTO> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageDTO> messages) {
        this.messages = messages;
    }
}
