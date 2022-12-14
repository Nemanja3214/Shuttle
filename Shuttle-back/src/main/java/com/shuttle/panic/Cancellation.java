package com.shuttle.panic;

import com.shuttle.ride.Ride;
import com.shuttle.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Cancellation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne
    private User user;
    @ManyToOne
    private Ride ride;
    private LocalDateTime time;
    private String reason;

}
