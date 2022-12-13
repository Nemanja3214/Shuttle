package com.shuttle.message;

import java.time.LocalDateTime;

import com.shuttle.common.Entity;
import com.shuttle.ride.Ride;
import com.shuttle.user.User;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@jakarta.persistence.Entity
public class Message extends Entity {
    @ManyToOne
    private User sender;
    @ManyToOne
    private User receiver;
    private String message;
    private LocalDateTime time;
    @ManyToOne
    private Ride ride;
    private Type type;

    public enum Type {
        Support, Ride, Panic
    }
}
