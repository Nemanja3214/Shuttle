package com.shuttle.ride.cancellation;

import com.shuttle.ride.Ride;
import com.shuttle.user.GenericUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Cancellation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne
    private GenericUser user;
    @ManyToOne // TODO: Should be OneToOne?
    private Ride ride;
    private LocalDateTime time;
    private String reason;
}
