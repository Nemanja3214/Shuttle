package com.shuttle.passenger;

import java.util.Set;

import com.shuttle.ride.Ride;
import com.shuttle.user.User;
import com.shuttle.vehicle.Route;

public class Passenger extends User {
    public Passenger(Long id, String name, String surname, String profilePicture, String telephoneNumber, String email, String address, String password) {
        super(id, name, surname, profilePicture, telephoneNumber, email, address, password);
    }


    private Set<Ride> rides;
    private Set<Route> favoriteRoutes;

    public Set<Ride> getRides() {
        return rides;
    }

    public void setRides(Set<Ride> rides) {
        this.rides = rides;
    }

    public Set<Route> getFavoriteRoutes() {
        return favoriteRoutes;
    }

    public void setFavoriteRoutes(Set<Route> favoriteRoutes) {
        this.favoriteRoutes = favoriteRoutes;
    }
}
