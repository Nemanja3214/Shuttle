package com.shuttle.panic;

import com.shuttle.ride.Ride;
import com.shuttle.user.GenericUser;

public interface IPanicService {
    public Panic add(Ride ride, GenericUser user, String message);
}
