package com.shuttle.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import com.shuttle.passenger.IPassengerRepository;
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
	
	@Autowired 
	private IPassengerRepository passengerRepository;
	
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
	
//	List<Ride> findStartedAcceptedPendingByPassenger(Long passengerId);
	
	@Test
	public void findStartedAcceptedPendingByPassenger_find_pending_and_accepted() {
		List<Ride> rides = this.rideRepository.findStartedAcceptedPendingByPassenger(3L);
	
		assertEquals(2, rides.size());
	}
	
	@Test
	public void findStartedAcceptedPendingByPassenger_find_0() {
		List<Ride> rides = this.rideRepository.findStartedAcceptedPendingByPassenger(11L);
	
		assertEquals(0, rides.size());
	}
	
//	findByDriverNull_
	@Test
	public void findByDriverNull_null() {
		List<Ride> ridesWithNoDriver = this.rideRepository.findByDriverNull();
		for(Ride ride : ridesWithNoDriver) {
			assertNull(ride.getDriver());
		}
		assertEquals(1, ridesWithNoDriver.size());
	}
	
//	  public List<Ride> findPendingInTheFuture();
	
	public void findPendingInTheFuture_find_1_of_3() {
		List<Ride> ridesWithNoDriver = this.rideRepository.findPendingInTheFuture();
		
		assertEquals(1, ridesWithNoDriver.size());
		
		Ride r = ridesWithNoDriver.get(0);
		assertEquals(Status.PENDING, r.getStatus());
		assertTrue(r.getStartTime().isAfter(LocalDateTime.now()));
	}
	
//	    public List<Ride> findByUser(Long userId, Pageable pageable);

	@Test
	public void findByUser_passenger() {
		Passenger passenger = getDummyPassenger();
		List<Passenger> passengers = new ArrayList<>();
		passengers.add(passenger);
		
		Ride ride = new Ride();
		ride.setPassengers(passengers);
		this.rideRepository.save(ride);
		Pageable pageable = PageRequest.of(0, 5);
		List<Ride> rides = rideRepository.findByUser(passenger.getId(), pageable);
		
		assertEquals(1, rides.size());
		assertEquals(ride, rides.get(0));
	}
	
	@Test
	public void findByUser_driver() {
		Driver driver = getDummyDriver();
		driver = this.driverRepository.save(driver);
		
		Ride ride = new Ride();
		ride.setDriver(driver);
		this.rideRepository.save(ride);
		Pageable pageable = PageRequest.of(0, 5);
		List<Ride> rides = rideRepository.findByUser(ride.getDriver().getId(), pageable);
		
		assertEquals(1, rides.size());
		assertEquals(ride, rides.get(0));
	}
	
	
	@Test
	public void findByUser_driver_with_no_rides() {
		Pageable pageable = PageRequest.of(0, 5);
		List<Ride> rides = rideRepository.findByUser(5L, pageable);
		
		assertEquals(0, rides.size());
	}
	
	
	@Test
	public void findByUser_non_existant_id() {
		Pageable pageable = PageRequest.of(0, 5);
		List<Ride> rides = rideRepository.findByUser(53123123L, pageable);
		
		assertEquals(0, rides.size());
	}
	
	
	
	
	
	
	
	
	private Passenger getDummyPassenger() {
		Passenger passenger = new Passenger();
		passenger.setEmail("dqdqwdqwdqwd@gmail.com");
		passenger = this.passengerRepository.save(passenger);
		return passenger;
	}
	
	private Driver getDummyDriver() {
		Driver driver = new Driver();
		driver.setEmail("dqdqwdqwdqwd@gmail.com");
		driver = this.driverRepository.save(driver);
		return driver;
	}
}
