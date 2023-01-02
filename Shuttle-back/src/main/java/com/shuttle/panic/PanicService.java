package com.shuttle.panic;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shuttle.ride.Ride;
import com.shuttle.user.GenericUser;

@Service
public class PanicService implements IPanicService {
    @Autowired
    private IPanicRepository panicRepository;


    @Override
    public Panic add(Ride ride, GenericUser user, String message) {
        Panic p = new Panic();
        p.setReason(message);
        p.setUser(user);
        p.setRide(ride);
        p.setTime(LocalDateTime.now());
        p = panicRepository.save(p);
        return p;
    }
}
