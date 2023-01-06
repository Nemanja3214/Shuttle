package com.shuttle.ride.cancellation;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shuttle.ride.Ride;
import com.shuttle.user.GenericUser;

@Service
public class CancellationService implements ICancellationService {
	@Autowired
	private ICancellationRepository cancellationRepository;

	@Override
	public Cancellation create(String reason, GenericUser creator) {
		Cancellation cancellation = new Cancellation(null, creator, LocalDateTime.now(), reason);	
		cancellation = cancellationRepository.save(cancellation);	
		return cancellation;
	}

}
