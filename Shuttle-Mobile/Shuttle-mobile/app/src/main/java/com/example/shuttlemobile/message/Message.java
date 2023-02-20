package com.example.shuttlemobile.message;

import androidx.annotation.Nullable;

import com.example.shuttlemobile.common.Entity;
import com.example.shuttlemobile.ride.Ride;
import com.example.shuttlemobile.user.User;

import java.time.LocalDateTime;

public class Message extends Entity {
    private User sender;
    private User recipient;
    private String message;
    private LocalDateTime date;
    @Nullable
    private Ride ride;
    private Type type;

    public enum Type {
        SUPPORT, RIDE, PANIC
    }

    public Message(User sender, User recipient, String message, LocalDateTime date, @Nullable Ride ride, Type type) {
        this.sender = sender;
        this.recipient = recipient;
        this.message = message;
        this.date = date;
        this.ride = ride;
        this.type = type;
    }

//    public Message(User sender, User recipient, String message) {
//        this.sender = sender;
//        this.recipient = recipient;
//        this.message = message;
//        this.date = LocalDateTime.now();
//    }

    public Message() {

    }

    public User getOther(User self) {
        if (getSender() == self) {
            return getRecipient();
        } else if (getRecipient() == self) {
            return getSender();
        } else {
            throw new IllegalArgumentException("'self' is not a sender nor a recipient of this message.");
        }
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Nullable
    public Ride getRide() {
        return ride;
    }

    public void setRide(@Nullable Ride ride) {
        this.ride = ride;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
