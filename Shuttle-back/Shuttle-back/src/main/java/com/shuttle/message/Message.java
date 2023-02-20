package com.shuttle.message;

import java.time.LocalDateTime;

import com.shuttle.ride.Ride;
import com.shuttle.user.GenericUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne
    private GenericUser sender;
    @ManyToOne
    private GenericUser receiver;
    private String message;
    private LocalDateTime time;
    @ManyToOne
    private Ride ride; // Can be null if SUPPORT.
    private Type type;

    public enum Type {
        SUPPORT, RIDE, PANIC
    }
}
