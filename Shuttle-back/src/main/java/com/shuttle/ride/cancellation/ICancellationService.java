package com.shuttle.ride.cancellation;

import com.shuttle.ride.Ride;
import com.shuttle.user.GenericUser;

public interface ICancellationService {
	/**
	 * Create a ride Cancellation. Note that this doesn't affect the ride in question at all.
	 * @param ride Ride object.
	 * @param reason Reason.
	 * @param creator User which cancelled the ride.
	 * @return The cancellation object that was just created.
	 */
	public Cancellation create(Ride ride, String reason, GenericUser creator);
}
