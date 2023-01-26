package com.shuttle.controller;

import java.util.ArrayList;
import java.util.List;

import com.shuttle.driver.Driver;
import com.shuttle.location.Route;
import com.shuttle.ride.Ride;
import com.shuttle.security.Role;
import com.shuttle.user.GenericUser;
import com.shuttle.vehicle.vehicleType.VehicleType;

public abstract class MockProvider {
	protected static GenericUser user(Long id, String email, String role) {
    	GenericUser u = new GenericUser();
    	List<Role> roles = new ArrayList<>();
    	roles.add(new Role(1L, role));
    	
    	u.setId(id);
    	u.setEmail(email);
    	u.setRoles(roles);
    	return u;
    }
    
    protected static Driver driver(Long id, String email, String role) {
    	Driver u = new Driver();
    	List<Role> roles = new ArrayList<>();
    	roles.add(new Role(1L, role));
    	
    	u.setId(id);
    	u.setEmail(email);
    	u.setRoles(roles);
    	return u;
    }
    
    protected static Ride rideBase(Long rideId, Ride.Status status) {
    	Ride ride = new Ride();	
    	Route r = new Route();
    	r.setLocations(new ArrayList<>());
    	
    	ride.setId(rideId);
    	ride.setStatus(status);
    	ride.setPassengers(new ArrayList<>());
    	ride.setVehicleType(new VehicleType(1L, "", 123));
    	ride.setRoute(r);
    	return ride;
    }
}
