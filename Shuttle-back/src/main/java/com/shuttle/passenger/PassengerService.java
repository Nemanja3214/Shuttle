package com.shuttle.passenger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PassengerService implements IPassengerService {
    @Autowired
    private IPassengerRepository passengerRepository;

    @Override
    public Passenger findByEmail(String email) {
        return passengerRepository.findByEmail(email);
    }

    @Override
    public Passenger findById(Long passengerId) {
        return passengerRepository.findById(passengerId).orElse(null);
    }
    
}
