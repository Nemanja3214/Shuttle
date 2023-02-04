package com.shuttle.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import com.shuttle.driver.Driver;
import com.shuttle.driver.IDriverRepository;
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
	
	@Autowired
	private IDriverRepository driverRepository;
	
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
	
	@Test
	public void findById_null() {	
		Optional<Ride> ride = rideRepository.findById(12312312312L);
		assertThat(ride).isEmpty();
	}
	
	@Test
	public void findByStatusAndDriver_pending() {
		Driver d = driverRepository.findById(1L).get();
		List<Ride> rides = rideRepository.findByDriverAndStatus(d, Status.PENDING);
		
		assertEquals(1, rides.size());
		assertEquals(Status.PENDING, rides.get(0).getStatus());
		
	}
	
	@Test
	public void findByStatusAndDriver_accepted() {
		Driver d = driverRepository.findById(1L).get();
		List<Ride> rides = rideRepository.findByDriverAndStatus(d, Status.ACCEPTED);
		
		assertEquals(1, rides.size());
		assertEquals(Status.ACCEPTED, rides.get(0).getStatus());
	}
	
	@Test
	public void findByStatusAndDriver_empty_with_that_status() {
		Driver d = driverRepository.findById(1L).get();
		List<Ride> rides = rideRepository.findByDriverAndStatus(d, Status.CANCELED);
		
		assertEquals(0, rides.size());
	}
	
	@Test
	public void findByStatusAndDriver_empty_with_that_driver() {
		Driver d = driverRepository.findById(5L).get();
		List<Ride> rides = rideRepository.findByDriverAndStatus(d, Status.CANCELED);
		
		assertEquals(0, rides.size());
	}
	
//	get all between dates
	@Test
	public void getAllBetweenDates_both_times_get_in() {
		LocalDateTime start = LocalDateTime.of(2016, 11, 15, 14, 0);
		LocalDateTime end = LocalDateTime.of(2020, 11, 16, 20, 0);
		Pageable pageable = PageRequest.of(0, 5);
		Page page = rideRepository.getAllBetweenDates(start, end, 6L, pageable);
		
		assertEquals(2, page.getNumberOfElements());
	}
	
	@Test
	public void getAllBetweenDates_first_time_gets_in() {
		LocalDateTime start = LocalDateTime.of(2017, 11, 15, 14, 0);
		LocalDateTime end = LocalDateTime.of(2017, 11, 16, 16, 0);
		Pageable pageable = PageRequest.of(0, 5);
		Page page = rideRepository.getAllBetweenDates(start, end, 6L, pageable);
		
		
		assertEquals(1, page.getNumberOfElements());
	}
	
	@Test
	public void getAllBetweenDates_second_time_gets_in() {
		LocalDateTime start = LocalDateTime.of(2017, 11, 16, 16, 0);
		LocalDateTime end = LocalDateTime.of(2022, 11, 16, 16, 0);
		Pageable pageable = PageRequest.of(0, 5);
		Page page = rideRepository.getAllBetweenDates(start, end, 6L, pageable);
		
		assertEquals(1, page.getNumberOfElements());
	}
	
	@Test
	public void getAllBetweenDates_neither_time_gets_in() {
		LocalDateTime start = LocalDateTime.of(2016, 11, 16, 16, 0);
		LocalDateTime end = LocalDateTime.of(2016, 11, 16, 16, 0);
		Pageable pageable = PageRequest.of(0, 5);
		Page page = rideRepository.getAllBetweenDates(start, end, 6L, pageable);
		
		assertEquals(0, page.getNumberOfElements());
	}
	
	
	
}
