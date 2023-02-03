package com.shuttle.panic;

import java.util.List;

import com.shuttle.ride.Ride;
import com.shuttle.user.GenericUser;
import org.springframework.data.domain.Pageable;

public interface IPanicService {
    public Panic add(Ride ride, GenericUser user, String message);
    public List<Panic> getAll();

    public List<Panic> findAll(Pageable pageable);
}