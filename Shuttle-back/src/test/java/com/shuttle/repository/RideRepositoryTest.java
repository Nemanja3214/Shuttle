package com.shuttle.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import com.shuttle.driver.Driver;
import com.shuttle.location.Route;
import com.shuttle.passenger.Passenger;
import com.shuttle.ride.IRideRepository;
import com.shuttle.ride.Ride;
import com.shuttle.ride.Ride.Status;
import com.shuttle.ride.cancellation.Cancellation;
import com.shuttle.vehicle.vehicleType.VehicleType;

@DataJpaTest
@ActiveProfiles("test")
public class RideRepositoryTest {
	@Autowired
	private IRideRepository rideRepository;
	
	@Test
	@DisplayName("Saves a new ride into the database")
	public void saveRide() {
		LocalDateTime startTime = LocalDateTime.now();
		LocalDateTime endTime = startTime.plusHours(1);
		
		Driver driver = new Driver();
		
		Passenger passenger1 = new Passenger();
		Passenger passenger2 = new Passenger();
		List<Passenger> passengers = new ArrayList<>();
		passengers.add(passenger1);
		passengers.add(passenger2);
		
		Route route = new Route();
		
		Integer estTimeInMin = 60;
		Boolean baby = false;
		Boolean pet = true;
		VehicleType vehicleType = new VehicleType();
		Cancellation cancellation = null;
		LocalDateTime scheduledTime = null;
		Double totalLen = 1357.0;
		Status status = Status.PENDING;
		
		Ride ride = new Ride(
				null, 
				startTime,
				endTime, 
				totalLen, 
				driver, 
				passengers, 
				route, 
				estTimeInMin, 
				baby, 
				pet, 
				vehicleType, 
				cancellation, 
				status, 
				scheduledTime, 
				totalLen
		);
		
		Ride savedRide = rideRepository.save(ride);
		
		assertThat(savedRide).usingRecursiveComparison().isEqualTo(ride);
		System.out.println(savedRide);
		System.out.println(ride);
	}
	
	@Test
	public void findById_works() {
		Optional<Ride> ride = rideRepository.findById(1L);
		assertThat(ride).isNotEmpty();
	}
}
