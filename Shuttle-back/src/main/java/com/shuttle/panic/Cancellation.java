package com.shuttle.panic;

import com.shuttle.ride.Ride;
import com.shuttle.user.GenericUser;
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
    private GenericUser user;
    @ManyToOne // TODO: Should be OneToOne
    private Ride ride;
    private LocalDateTime time;
    private String reason;

}
