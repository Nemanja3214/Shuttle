package com.shuttle.panic;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
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
        p.setTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        p = panicRepository.save(p);
        return p;
    }


	@Override
	public List<Panic> getAll() {
		return panicRepository.findAll();
	}

    @Override
    public List<Panic> findAll(Pageable pageable) {
        return panicRepository.findAll(pageable).getContent();
    }
}
