package com.shuttle.panic;

import com.shuttle.common.Entity;
import com.shuttle.ride.Ride;
import com.shuttle.user.User;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@jakarta.persistence.Entity
public class Cancelation extends Entity {

    @ManyToOne
    private User user;
    @ManyToOne
    private Ride ride;
    private LocalDateTime time;
    private String reason;

}
