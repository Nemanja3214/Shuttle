package com.shuttle.passenger;

public interface IPassengerService {
    /**
     * Find passenger by email.
     * @param email The email.
     * @return The passenger with such email or null if none found.
     */
    public Passenger findByEmail(String email);

    /**
     * Find passenger by id.
     * @param passengerId ID.
     * @return The passenger with the provided ID or null if none found.
     */
    public Passenger findById(Long passengerId);
}
