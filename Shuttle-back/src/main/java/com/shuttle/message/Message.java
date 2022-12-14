package com.shuttle.message;

import java.time.LocalDateTime;

import com.shuttle.ride.Ride;
import com.shuttle.user.User;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
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
