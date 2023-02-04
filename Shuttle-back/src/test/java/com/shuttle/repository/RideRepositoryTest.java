package com.shuttle.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import com.shuttle.driver.Driver;
import com.shuttle.driver.IDriverRepository;
import com.shuttle.location.Route;
import com.shuttle.passenger.IPassengerRepository;
import com.shuttle.passenger.Passenger;
import com.shuttle.ride.IRideRepository;
import com.shuttle.ride.Ride;
import com.shuttle.ride.Ride.Status;
import com.shuttle.ride.cancellation.Cancellation;
import com.shuttle.ride.dto.GraphEntryDTO;
import com.shuttle.vehicle.vehicleType.VehicleType;


// TODO testiraj page mozda

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
		Ride ride = new Ride();
		ride = rideRepository.save(ride);
		Optional<Ride> rideO = rideRepository.findById(ride.getId());
		assertThat(rideO).isNotEmpty();
	}
	
	@Test
	public void findById_null() {	
		Optional<Ride> ride = rideRepository.findById(12312312312L);
		assertThat(ride).isEmpty();
	}
	
	@Test
	public void findByStatusAndDriver_pending() {
		Driver driver = getDummyDriver();
		
		Ride ride = new Ride();
		ride.setDriver(driver);
		ride.setStatus(Status.PENDING);
		rideRepository.save(ride);
		
		List<Ride> rides = rideRepository.findByDriverAndStatus(driver, Status.PENDING);
		
		assertEquals(1, rides.size());
		assertEquals(Status.PENDING, rides.get(0).getStatus());
		
	}
	
	@Test
	public void findByStatusAndDriver_accepted() {
		Driver driver = getDummyDriver();
		
		Ride ride = new Ride();
		ride.setDriver(driver);
		ride.setStatus(Status.ACCEPTED);
		rideRepository.save(ride);
		
		List<Ride> rides = rideRepository.findByDriverAndStatus(driver, Status.ACCEPTED);
		
		assertEquals(1, rides.size());
		assertEquals(Status.ACCEPTED, rides.get(0).getStatus());
	}
	
	@Test
	public void findByStatusAndDriver_empty_with_that_status() {
		Driver driver = getDummyDriver();
		
		Ride ride = new Ride();
		ride.setDriver(driver);
		ride.setStatus(Status.ACCEPTED);
		rideRepository.save(ride);
		
		List<Ride> rides = rideRepository.findByDriverAndStatus(driver, Status.CANCELED);
		
		assertEquals(0, rides.size());
	}
	
	@Test
	public void findByStatusAndDriver_empty_with_that_driver() {
		Driver driver = getDummyDriver();
		
		Ride ride = new Ride();
		ride.setStatus(Status.ACCEPTED);
		rideRepository.save(ride);
		
		List<Ride> rides = rideRepository.findByDriverAndStatus(driver, Status.ACCEPTED);
		
		assertEquals(0, rides.size());
	}
	
//	get all between dates
	@Test
	public void getAllBetweenDates_both_times_get_in() {
		Driver driver = getDummyDriver();
		
		LocalDateTime startQuery = LocalDateTime.of(2016, 11, 15, 14, 0);
		LocalDateTime endQuery = LocalDateTime.of(2022, 11, 16, 20, 0);
		
//		ride #1
		LocalDateTime start = LocalDateTime.of(2017, 11, 15, 14, 0);
		LocalDateTime end = LocalDateTime.of(2018, 11, 16, 20, 0);
		Ride rideBefore = new Ride();
		rideBefore.setDriver(driver);
		rideBefore.setStartTime(start);
		rideBefore.setEndTime(end);
		rideRepository.save(rideBefore);
		
//		ride #2
		start = LocalDateTime.of(2019, 11, 15, 14, 0);
		end = LocalDateTime.of(2020, 11, 16, 20, 0);
		Ride rideAfter = new Ride();
		rideAfter.setDriver(driver);
		rideAfter.setStartTime(start);
		rideAfter.setEndTime(end);
		rideRepository.save(rideAfter);

		Pageable pageable = PageRequest.of(0, 5);
		Page page = rideRepository.getAllBetweenDates(startQuery, endQuery, driver.getId(), pageable);
		
		assertEquals(2, page.getNumberOfElements());
		assertThat(page.getContent()).contains(rideBefore);
		assertThat(page.getContent()).contains(rideAfter);
	}
	
	@Test
	public void getAllBetweenDates_first_time_gets_in() {
		Driver driver = getDummyDriver();
		
		LocalDateTime startQuery = LocalDateTime.of(2016, 11, 15, 14, 0);
		LocalDateTime endQuery = LocalDateTime.of(2019, 12, 16, 20, 0);
		
//		ride #1
		LocalDateTime start = LocalDateTime.of(2017, 11, 15, 14, 0);
		LocalDateTime end = LocalDateTime.of(2018, 11, 16, 20, 0);
		Ride rideBefore = new Ride();
		rideBefore.setDriver(driver);
		rideBefore.setStartTime(start);
		rideBefore.setEndTime(end);
		rideRepository.save(rideBefore);
		
//		ride #2
		start = LocalDateTime.of(2019, 11, 15, 14, 0);
		end = LocalDateTime.of(2020, 11, 16, 20, 0);
		Ride rideAfter = new Ride();
		rideAfter.setDriver(driver);
		rideAfter.setStartTime(start);
		rideAfter.setEndTime(end);
		rideRepository.save(rideAfter);
		
		Pageable pageable = PageRequest.of(0, 5);
		Page page = rideRepository.getAllBetweenDates(startQuery, endQuery, driver.getId(), pageable);
		
		
		assertEquals(1, page.getNumberOfElements());
		assertThat(page.getContent()).contains(rideBefore);
	}
	
	@Test
	public void getAllBetweenDates_second_time_gets_in() {
		Driver driver = getDummyDriver();
		
		LocalDateTime startQuery = LocalDateTime.of(2019, 11, 15, 14, 0);
		LocalDateTime endQuery = LocalDateTime.of(2023, 12, 16, 20, 0);
		
//		ride #1
		LocalDateTime start = LocalDateTime.of(2017, 11, 15, 14, 0);
		LocalDateTime end = LocalDateTime.of(2018, 11, 16, 20, 0);
		Ride rideBefore = new Ride();
		rideBefore.setDriver(driver);
		rideBefore.setStartTime(start);
		rideBefore.setEndTime(end);
		rideRepository.save(rideBefore);
		
//		ride #2
		start = LocalDateTime.of(2019, 11, 15, 14, 0);
		end = LocalDateTime.of(2020, 11, 16, 20, 0);
		Ride rideAfter = new Ride();
		rideAfter.setDriver(driver);
		rideAfter.setStartTime(start);
		rideAfter.setEndTime(end);
		rideRepository.save(rideAfter);
		
		Pageable pageable = PageRequest.of(0, 5);
		Page page = rideRepository.getAllBetweenDates(startQuery, endQuery, driver.getId(), pageable);
		
		assertEquals(1, page.getNumberOfElements());
		assertThat(page.getContent()).contains(rideAfter);
	}
	
	@Test
	public void getAllBetweenDates_neither_time_gets_in() {
		Driver driver = getDummyDriver();
		
		LocalDateTime startQuery = LocalDateTime.of(2033, 11, 15, 14, 0);
		LocalDateTime endQuery = LocalDateTime.of(2034, 12, 16, 20, 0);
		
//		ride #1
		LocalDateTime start = LocalDateTime.of(2017, 11, 15, 14, 0);
		LocalDateTime end = LocalDateTime.of(2018, 11, 16, 20, 0);
		Ride rideBefore = new Ride();
		rideBefore.setDriver(driver);
		rideBefore.setStartTime(start);
		rideBefore.setEndTime(end);
		rideRepository.save(rideBefore);
		
//		ride #2
		start = LocalDateTime.of(2019, 11, 15, 14, 0);
		end = LocalDateTime.of(2020, 11, 16, 20, 0);
		Ride rideAfter = new Ride();
		rideAfter.setDriver(driver);
		rideAfter.setStartTime(start);
		rideAfter.setEndTime(end);
		rideRepository.save(rideAfter);
		
		Pageable pageable = PageRequest.of(0, 5);
		Page page = rideRepository.getAllBetweenDates(startQuery, endQuery, driver.getId(), pageable);
		
		assertEquals(0, page.getNumberOfElements());
	}
	
//	List<Ride> findStartedAcceptedPendingByPassenger(Long passengerId);
	
	@Test
	public void findStartedAcceptedPendingByPassenger_find_pending_and_accepted() {
		Passenger passenger = getDummyPassenger();
		List<Passenger> passengers = new ArrayList<>();
		passengers.add(passenger);
		
		Ride ridePending = new Ride();
		ridePending.setPassengers(passengers);
		ridePending.setStatus(Ride.Status.PENDING);
		rideRepository.save(ridePending);
		
		Ride rideAccepted = new Ride();
		rideAccepted.setPassengers(passengers);
		rideAccepted.setStatus(Ride.Status.ACCEPTED);
		rideRepository.save(rideAccepted);
		
		Ride rideFinished = new Ride();
		rideFinished.setPassengers(passengers);
		rideFinished.setStatus(Ride.Status.FINISHED);
		rideRepository.save(rideFinished);
		
		Ride rideCanceled = new Ride();
		rideCanceled.setPassengers(passengers);
		rideCanceled.setStatus(Ride.Status.CANCELED);
		rideRepository.save(rideCanceled);
		List<Ride> rides = this.rideRepository.findStartedAcceptedPendingByPassenger(passenger.getId());
	
		assertEquals(2, rides.size());
	}
	
	@Test
	public void findStartedAcceptedPendingByPassenger_find_0() {
		List<Ride> rides = this.rideRepository.findStartedAcceptedPendingByPassenger(11234234L);
	
		assertEquals(0, rides.size());
	}
	
//	findByDriverNull
	@Test
	public void findByDriverNull_null() {
		Driver driver = getDummyDriver();
		
		Ride ridePending = new Ride();
		ridePending.setDriver(driver);
		ridePending.setStatus(Ride.Status.PENDING);
		rideRepository.save(ridePending);
		
		Ride rideAccepted = new Ride();
		rideAccepted.setDriver(driver);
		rideAccepted.setStatus(Ride.Status.ACCEPTED);
		rideRepository.save(rideAccepted);
		
		Ride rideFinished = new Ride();
		rideFinished.setStatus(Ride.Status.FINISHED);
		rideRepository.save(rideFinished);
		
		Ride rideCanceled = new Ride();
		rideCanceled.setStatus(Ride.Status.CANCELED);
		rideRepository.save(rideCanceled);
		List<Ride> ridesWithNoDriver = this.rideRepository.findByDriverNull();
		
		for(Ride ride : ridesWithNoDriver) {
			assertNull(ride.getDriver());
		}
		assertEquals(2, ridesWithNoDriver.size());
		assertThat(ridesWithNoDriver).contains(rideFinished);
		assertThat(ridesWithNoDriver).contains(rideCanceled);
	}
	
//	  public List<Ride> findPendingInTheFuture();
	
	public void findPendingInTheFuture_find_1_of_3() {
		
		Ride ridePendingInPast = new Ride();
		ridePendingInPast.setStartTime(LocalDateTime.now().minusSeconds(1));
		ridePendingInPast.setStatus(Ride.Status.PENDING);
		rideRepository.save(ridePendingInPast);
		
		Ride ridePendingInFuture = new Ride();
		ridePendingInFuture.setStartTime(LocalDateTime.now().plusHours(1));
		ridePendingInFuture.setStatus(Ride.Status.PENDING);
		rideRepository.save(ridePendingInFuture);
		
		Ride rideFinishedInFuture = new Ride();
		rideFinishedInFuture.setStartTime(LocalDateTime.now().plusHours(1));
		rideFinishedInFuture.setStatus(Ride.Status.FINISHED);
		rideRepository.save(rideFinishedInFuture);
		
		Ride rideFinishedInPast = new Ride();
		rideFinishedInPast.setStartTime(LocalDateTime.now().minusSeconds(1));
		rideFinishedInPast.setStatus(Ride.Status.FINISHED);
		rideRepository.save(rideFinishedInPast);
		
		List<Ride> rides = this.rideRepository.findPendingInTheFuture();
		
		assertEquals(1, rides.size());
		
		Ride r = rides.get(0);
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
	
	
//	   public List<Ride> findByUser(Long userId, Pageable pageable, LocalDateTime dateFrom, LocalDateTime dateTo);
	
	@Test
	public void findByUserDate_happy() {
		LocalDateTime queryStart = LocalDateTime.of(2016, 11, 16, 16, 0);
		LocalDateTime queryEnd = LocalDateTime.of(2023, 11, 16, 16, 0);
		
		LocalDateTime start = LocalDateTime.of(2017, 11, 16, 16, 0);
		LocalDateTime end = LocalDateTime.of(2017, 11, 16, 16, 0);
		Driver driver = getDummyDriver();
		
		Ride ride = new Ride();
		ride.setDriver(driver);
		ride.setStartTime(start);
		ride.setEndTime(end);
		this.rideRepository.save(ride);
		Pageable pageable = PageRequest.of(0, 5);
		List<Ride> rides = rideRepository.findByUser(ride.getDriver().getId(), pageable, queryStart, queryEnd);
		
		assertEquals(1, rides.size());
		assertEquals(ride, rides.get(0));
	}
	
	@Test
	public void findByUserDate_driver_but_date_dont_match() {
		LocalDateTime queryStart = LocalDateTime.of(2016, 11, 16, 16, 0);
		LocalDateTime queryEnd = LocalDateTime.of(2017, 11, 16, 16, 0);
		
		LocalDateTime start = LocalDateTime.of(2017, 11, 16, 16, 0);
		LocalDateTime end = LocalDateTime.of(2017, 11, 16, 17, 0);
		Driver driver = getDummyDriver();
		
		Ride ride = new Ride();
		ride.setDriver(driver);
		ride.setStartTime(start);
		ride.setEndTime(end);
		this.rideRepository.save(ride);
		Pageable pageable = PageRequest.of(0, 5);
		List<Ride> rides = rideRepository.findByUser(ride.getDriver().getId(), pageable, queryStart, queryEnd);
		
		assertEquals(0, rides.size());
	}
	
	@Test
	public void findByUserDate_date_but_user_dont_match() {
		LocalDateTime queryStart = LocalDateTime.of(2015, 11, 16, 16, 0);
		LocalDateTime queryEnd = LocalDateTime.of(2022, 11, 16, 16, 0);
		
		LocalDateTime start = LocalDateTime.of(2017, 11, 16, 16, 0);
		LocalDateTime end = LocalDateTime.of(2017, 11, 16, 17, 0);
		Driver driver = getDummyDriver();
		
		Ride ride = new Ride();
		ride.setDriver(driver);
		ride.setStartTime(start);
		ride.setEndTime(end);
		this.rideRepository.save(ride);
		Pageable pageable = PageRequest.of(0, 5);
		List<Ride> rides = rideRepository.findByUser(123123L, pageable, queryStart, queryEnd);
		
		assertEquals(0, rides.size());
	}
	
	@Test
	public void findByUserDate_happy_with_end_null() {
		LocalDateTime queryStart = LocalDateTime.of(2015, 11, 16, 16, 0);
		LocalDateTime queryEnd = LocalDateTime.of(2022, 11, 16, 16, 0);
		
		LocalDateTime start = LocalDateTime.of(2017, 11, 16, 16, 0);
		LocalDateTime end = null;
		Driver driver = getDummyDriver();
		
		Ride ride = new Ride();
		ride.setDriver(driver);
		ride.setStartTime(start);
		ride.setEndTime(end);
		this.rideRepository.save(ride);
		Pageable pageable = PageRequest.of(0, 5);
		List<Ride> rides = rideRepository.findByUser(driver.getId(), pageable, queryStart, queryEnd);
		
		assertEquals(1, rides.size());
		assertEquals(ride, rides.get(0));
	}
	
	@Test
	public void findByUserDate_end_null_but_driver_dont_exist() {
		LocalDateTime queryStart = LocalDateTime.of(2015, 11, 16, 16, 0);
		LocalDateTime queryEnd = LocalDateTime.of(2022, 11, 16, 16, 0);
		
		LocalDateTime start = LocalDateTime.of(2017, 11, 16, 16, 0);
		LocalDateTime end = null;
		Driver driver = getDummyDriver();
		
		Ride ride = new Ride();
		ride.setDriver(driver);
		ride.setStartTime(start);
		ride.setEndTime(end);
		this.rideRepository.save(ride);
		Pageable pageable = PageRequest.of(0, 5);
		List<Ride> rides = rideRepository.findByUser(1231234L, pageable, queryStart, queryEnd);
		
		assertEquals(0, rides.size());
	}
	
//    public List<Ride> getAllByPassengerAndBetweenDates(LocalDateTime startDate, LocalDateTime endDate, Passenger passenger, Pageable pageable);
	
	@Test
	public void getAllByPassengerAndBetweenDates_happy() {
		LocalDateTime queryStart = LocalDateTime.of(2015, 11, 16, 16, 0);
		LocalDateTime queryEnd = LocalDateTime.of(2022, 11, 16, 16, 0);
		
		LocalDateTime start = LocalDateTime.of(2017, 11, 16, 16, 0);
		LocalDateTime end = LocalDateTime.of(2017, 11, 17, 16, 0);
		
		Passenger passenger = getDummyPassenger();
		List<Passenger> passengers = new ArrayList<>();
		passengers.add(passenger);
		
		Ride ride = new Ride();
		ride.setStartTime(start);
		ride.setEndTime(end);
		ride.setPassengers(passengers);
		this.rideRepository.save(ride);
		Pageable pageable = PageRequest.of(0, 5);
		List<Ride> rides = rideRepository.findByUser(passenger.getId(), pageable, queryStart, queryEnd);
		
		assertEquals(1, rides.size());
		assertEquals(ride, rides.get(0));
	}
	
	@Test
	public void getAllByPassengerAndBetweenDates_date_dont_match() {
		LocalDateTime queryStart = LocalDateTime.of(2022, 11, 16, 16, 0);
		LocalDateTime queryEnd = LocalDateTime.of(2023, 11, 16, 16, 0);
		
		LocalDateTime end = LocalDateTime.of(2017, 11, 17, 16, 0);
		
		Passenger passenger = getDummyPassenger();
		List<Passenger> passengers = new ArrayList<>();
		passengers.add(passenger);
		
		Ride ride = new Ride();
		ride.setEndTime(end);
		ride.setPassengers(passengers);
		this.rideRepository.save(ride);
		Pageable pageable = PageRequest.of(0, 5);
		List<Ride> rides = rideRepository.findByUser(passenger.getId(), pageable, queryStart, queryEnd);
		
		assertEquals(0, rides.size());
	}
	
	@Test
	public void getAllByPassengerAndBetweenDates_happy_end_null() {
		LocalDateTime queryStart = LocalDateTime.of(2016, 11, 16, 16, 0);
		LocalDateTime queryEnd = LocalDateTime.of(2023, 11, 16, 16, 0);
		
		LocalDateTime end = null;
		
		Passenger passenger = getDummyPassenger();
		List<Passenger> passengers = new ArrayList<>();
		passengers.add(passenger);
		
		Ride ride = new Ride();
		ride.setEndTime(end);
		ride.setPassengers(passengers);
		this.rideRepository.save(ride);
		Pageable pageable = PageRequest.of(0, 5);
		List<Ride> rides = rideRepository.findByUser(passenger.getId(), pageable, queryStart, queryEnd);
		
		assertEquals(1, rides.size());
		assertEquals(ride, rides.get(0));
	}
	
	@Test
	public void getAllByPassengerAndBetweenDates_happy_with_page_less_size() {
		LocalDateTime queryStart = LocalDateTime.of(2016, 11, 16, 16, 0);
		LocalDateTime queryEnd = LocalDateTime.of(2023, 11, 16, 16, 0);
		
		LocalDateTime end = LocalDateTime.of(2017, 11, 16, 16, 0);
		
		Passenger passenger = getDummyPassenger();
		List<Passenger> passengers = new ArrayList<>();
		passengers.add(passenger);
		
//		ride #1
		Ride ride = new Ride();
		ride.setEndTime(end);
		ride.setPassengers(passengers);
		this.rideRepository.save(ride);
		
//		ride #2
		Ride ride2 = new Ride();
		ride2.setEndTime(end);
		ride2.setPassengers(passengers);
		this.rideRepository.save(ride2);
		
		Pageable pageable = PageRequest.of(0, 1);
		List<Ride> rides = rideRepository.findByUser(passenger.getId(), pageable, queryStart, queryEnd);
		
		assertEquals(1, rides.size());
		assertEquals(ride, rides.get(0));
	}
	
	@Test
	public void getAllByPassengerAndBetweenDates_happy_multiple_rides() {
		LocalDateTime queryStart = LocalDateTime.of(2016, 11, 16, 16, 0);
		LocalDateTime queryEnd = LocalDateTime.of(2023, 11, 16, 16, 0);
		
		LocalDateTime end = LocalDateTime.of(2017, 11, 16, 16, 0);;
		
		Passenger passenger = getDummyPassenger();
		List<Passenger> passengers = new ArrayList<>();
		passengers.add(passenger);
		
//		ride #1
		Ride ride = new Ride();
		ride.setEndTime(end);
		ride.setPassengers(passengers);
		this.rideRepository.save(ride);
		
//		ride #2
		Ride ride2 = new Ride();
		ride2.setEndTime(end);
		ride2.setPassengers(passengers);
		this.rideRepository.save(ride2);
		
		Pageable pageable = PageRequest.of(0, 5);
		List<Ride> rides = rideRepository.findByUser(passenger.getId(), pageable, queryStart, queryEnd);
		
		assertEquals(2, rides.size());
		assertTrue(rides.contains(ride));
		assertTrue(rides.contains(ride2));
	}
	
	@Test
	public void getAllByPassengerAndBetweenDates_no_such_passenger() {
		LocalDateTime queryStart = LocalDateTime.of(2016, 11, 16, 16, 0);
		LocalDateTime queryEnd = LocalDateTime.of(2023, 11, 16, 16, 0);
		
		LocalDateTime end = LocalDateTime.of(2017, 11, 16, 16, 0);;
		
		Passenger passenger = getDummyPassenger();
		List<Passenger> passengers = new ArrayList<>();
		passengers.add(passenger);
		
//		ride #1
		Ride ride = new Ride();
		ride.setEndTime(end);
		ride.setPassengers(passengers);
		this.rideRepository.save(ride);
		
//		ride #2
		Ride ride2 = new Ride();
		ride2.setEndTime(end);
		ride2.setPassengers(passengers);
		this.rideRepository.save(ride2);
		
		Pageable pageable = PageRequest.of(0, 5);
		List<Ride> rides = rideRepository.findByUser(12314L, pageable, queryStart, queryEnd);
		
		assertEquals(0, rides.size());
	}
	
	
//	public List<GraphEntryDTO> getPassengerGraphData(LocalDateTime start, LocalDateTime end, long passengerId);
	
	@Test
	public void getPassengerGraphData_happy() {
		LocalDateTime queryStart = LocalDateTime.of(2009, 11, 16, 0, 0);
		LocalDateTime queryEnd = LocalDateTime.of(2012, 11, 16, 0, 0);
		
		LocalDateTime end = LocalDateTime.of(2011, 11, 16, 0, 0);
		Passenger passenger = getDummyPassenger();
		List<Passenger> passengers = new ArrayList<>();
		passengers.add(passenger);
		
//		sums for the day complement eachother to be 50 on all days
//		length is 100 per ride, 2 rides a day => total 200 per day
		for(int i = 0; i < 20; ++i) {
			end = end.plusDays(1);
//			ride #1
			Ride ride = new Ride();
			ride.setStatus(Status.FINISHED);
			ride.setEndTime(end);
			ride.setPassengers(passengers);
			
			double cost = 50 - i;
			ride.setTotalCost(cost);
			ride.setTotalLength(100D);
			this.rideRepository.save(ride);
			
			end = end.getHour() > 20 ? end.minusHours(1): end.plusHours(1);

//			ride #2
			Ride ride2 = new Ride();
			ride2.setEndTime(end);
			ride2.setStatus(Status.FINISHED);
			ride2.setPassengers(passengers);
			
			cost = i;
			ride2.setTotalCost(cost);
			ride2.setTotalLength(100D);
			this.rideRepository.save(ride2);
		}
		
		List<GraphEntryDTO> graphData = rideRepository.getPassengerGraphData(queryStart, queryEnd, passenger.getId());
		
		assertEquals(20, graphData.size());
		for(GraphEntryDTO entry : graphData) {
			assertEquals(50D, entry.getCostSum());
			assertEquals(200D, entry.getLength());
			assertEquals(2, entry.getNumberOfRides());
		}

	}
	
	@Test
	public void getPassengerGraphData_happy_half_interval() {
		LocalDateTime queryStart = LocalDateTime.of(2009, 11, 16, 0, 0);
		
		LocalDateTime end = LocalDateTime.of(2011, 11, 16, 0, 0);
		Passenger passenger = getDummyPassenger();
		List<Passenger> passengers = new ArrayList<>();
		passengers.add(passenger);
		
//		sums for the day complement eachother to be 50 on all days
//		length is 100 per ride, 2 rides a day => total 200 per day
		for(int i = 0; i < 20; ++i) {
			end = end.plusDays(1);
//			ride #1
			Ride ride = new Ride();
			ride.setStatus(Status.FINISHED);
			ride.setEndTime(end);
			ride.setPassengers(passengers);
			
			double cost = 50 - i;
			ride.setTotalCost(cost);
			ride.setTotalLength(100D);
			this.rideRepository.save(ride);
			
			end = end.getHour() > 20 ? end.minusHours(1): end.plusHours(1);

//			ride #2
			Ride ride2 = new Ride();
			ride2.setStatus(Status.FINISHED);
			ride2.setEndTime(end);
			ride2.setPassengers(passengers);
			
			cost = i;
			ride2.setTotalCost(cost);
			ride2.setTotalLength(100D);
			this.rideRepository.save(ride2);
		}
		LocalDateTime queryEnd = end.minusDays(10);
		
		List<GraphEntryDTO> graphData = rideRepository.getPassengerGraphData(queryStart, queryEnd, passenger.getId());
		
		assertEquals(10, graphData.size());
		for(GraphEntryDTO entry : graphData) {
			assertEquals(50D, entry.getCostSum());
			assertEquals(200D, entry.getLength());
			assertEquals(2, entry.getNumberOfRides());
		}
	}
	
	
	@Test
	public void getPassengerGraphData_non_existant_user() {
		LocalDateTime queryStart = LocalDateTime.of(2009, 11, 16, 0, 0);
		LocalDateTime queryEnd = LocalDateTime.of(2012, 11, 16, 0, 0);

		List<GraphEntryDTO> graphData = rideRepository.getPassengerGraphData(queryStart, queryEnd, 123123L);

		assertEquals(0, graphData.size());
	}
	
	@Test
	public void getPassengerGraphData_happy_half_are_finished() {
		LocalDateTime queryStart = LocalDateTime.of(2009, 11, 16, 0, 0);
		LocalDateTime queryEnd = LocalDateTime.of(2022, 11, 16, 0, 0);
		
		LocalDateTime end = LocalDateTime.of(2011, 11, 16, 0, 0);
		Passenger passenger = getDummyPassenger();
		List<Passenger> passengers = new ArrayList<>();
		passengers.add(passenger);
		
//		sums for the day complement eachother to be 50 on all days
//		length is 100 per ride, 2 rides a day => total 200 per day
		for(int i = 0; i < 20; ++i) {
			end = end.plusDays(1);
//			ride #1
			Ride ride = new Ride();
			ride.setStatus(Status.FINISHED);
			ride.setEndTime(end);
			ride.setPassengers(passengers);
			
			double cost = 50 - i;
			ride.setTotalCost(cost);
			ride.setTotalLength(100D);
			this.rideRepository.save(ride);
			
			end = end.getHour() > 20 ? end.minusHours(1): end.plusHours(1);

//			ride #2
			Ride ride2 = new Ride();
			ride2.setStatus(Status.CANCELED);
			ride2.setEndTime(end);
			ride2.setPassengers(passengers);
			
			cost = i;
			ride2.setTotalCost(cost);
			ride2.setTotalLength(100D);
			this.rideRepository.save(ride2);
		}
		
		List<GraphEntryDTO> graphData = rideRepository.getPassengerGraphData(queryStart, queryEnd, passenger.getId());
		
		assertEquals(20, graphData.size());
		for(int i = 0; i < 20; ++i) {
			assertEquals(50D - i, graphData.get(i).getCostSum());
			assertEquals(100D, graphData.get(i).getLength());
			assertEquals(1, graphData.get(i).getNumberOfRides());
		}
	}
	
	
	@Test
	public void getPassengerGraphData_passenger_with_no_rides() {
		LocalDateTime queryStart = LocalDateTime.of(2009, 11, 16, 0, 0);
		LocalDateTime queryEnd = LocalDateTime.of(2022, 11, 16, 0, 0);
		
		LocalDateTime end = LocalDateTime.of(2011, 11, 16, 0, 0);
		Passenger passenger = getDummyPassenger();
		
		List<GraphEntryDTO> graphData = rideRepository.getPassengerGraphData(queryStart, queryEnd, passenger.getId());
		
		assertEquals(0, graphData.size());
	}
	
	
	
	
	
	@Test
	public void getPassengerGraphData_happy_are_sorted() {
		LocalDateTime queryStart = LocalDateTime.of(2009, 11, 16, 0, 0);
		LocalDateTime queryEnd = LocalDateTime.of(2012, 11, 16, 0, 0);
		
		LocalDateTime end = LocalDateTime.of(2011, 11, 16, 0, 0);
		Passenger passenger = getDummyPassenger();
		List<Passenger> passengers = new ArrayList<>();
		passengers.add(passenger);
		
//		sums for the day complement eachother to be 50 on all days
//		length is 100 per ride, 2 rides a day => total 200 per day
		List<Ride> rides = new ArrayList<>();
		for(int i = 0; i < 20; ++i) {
			end = end.plusDays(1);
//			ride #1
			Ride ride = new Ride();
			ride.setStatus(Status.FINISHED);
			ride.setEndTime(end);
			ride.setPassengers(passengers);
			
			double cost = 50 - i;
			ride.setTotalCost(cost);
			ride.setTotalLength(100D);
			this.rideRepository.save(ride);
			rides.add(ride);
			
			end = end.getHour() > 20 ? end.minusHours(1): end.plusHours(1);

//			ride #2
			Ride ride2 = new Ride();
			ride2.setEndTime(end);
			ride2.setStatus(Status.FINISHED);
			ride2.setPassengers(passengers);
			
			cost = i;
			ride2.setTotalCost(cost);
			ride2.setTotalLength(100D);
			this.rideRepository.save(ride2);
			rides.add(ride2);
		}
		
		List<GraphEntryDTO> graphData = rideRepository.getPassengerGraphData(queryStart, queryEnd, passenger.getId());
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		Set<String> datesSet = new TreeSet<>(rides.stream().map(ride -> ride.getEndTime().format(formatter)).toList());
		List<String> dates = new ArrayList<>(datesSet);
		Collections.sort(dates);
		
		assertEquals(20, graphData.size());
		for(int i = 0; i < 20; ++i) {
			assertEquals(dates.get(i), graphData.get(i).getTime());
			assertEquals(50D, graphData.get(i).getCostSum());
			assertEquals(200D, graphData.get(i).getLength());
			assertEquals(2, graphData.get(i).getNumberOfRides());
		}

	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
//		public List<GraphEntryDTO> getDriverGraphData(LocalDateTime start, LocalDateTime end, long driverId);
	
	@Test
	public void getDriverGraphData_happy() {
		LocalDateTime queryStart = LocalDateTime.of(2009, 11, 16, 0, 0);
		LocalDateTime queryEnd = LocalDateTime.of(2012, 11, 16, 0, 0);
		
		LocalDateTime end = LocalDateTime.of(2011, 11, 16, 0, 0);
		Driver driver = getDummyDriver();
		
//		sums for the day complement eachother to be 50 on all days
//		length is 100 per ride, 2 rides a day => total 200 per day
		for(int i = 0; i < 20; ++i) {
			end = end.plusDays(1);
//			ride #1
			Ride ride = new Ride();
			ride.setStatus(Status.FINISHED);
			ride.setEndTime(end);
			ride.setDriver(driver);
			
			double cost = 50 - i;
			ride.setTotalCost(cost);
			ride.setTotalLength(100D);
			this.rideRepository.save(ride);
			
			end = end.getHour() > 20 ? end.minusHours(1): end.plusHours(1);

//			ride #2
			Ride ride2 = new Ride();
			ride2.setEndTime(end);
			ride2.setStatus(Status.FINISHED);
			ride2.setDriver(driver);
			
			cost = i;
			ride2.setTotalCost(cost);
			ride2.setTotalLength(100D);
			this.rideRepository.save(ride2);
		}
		
		List<GraphEntryDTO> graphData = rideRepository.getDriverGraphData(queryStart, queryEnd, driver.getId());
		
		assertEquals(20, graphData.size());
		for(GraphEntryDTO entry : graphData) {
			assertEquals(50D, entry.getCostSum());
			assertEquals(200D, entry.getLength());
			assertEquals(2, entry.getNumberOfRides());
		}

	}
	
	@Test
	public void getDriverGraphData_happy_half_interval() {
		LocalDateTime queryStart = LocalDateTime.of(2009, 11, 16, 0, 0);
		
		LocalDateTime end = LocalDateTime.of(2011, 11, 16, 0, 0);
		Driver driver = getDummyDriver();
		
//		sums for the day complement eachother to be 50 on all days
//		length is 100 per ride, 2 rides a day => total 200 per day
		for(int i = 0; i < 20; ++i) {
			end = end.plusDays(1);
//			ride #1
			Ride ride = new Ride();
			ride.setStatus(Status.FINISHED);
			ride.setEndTime(end);
			ride.setDriver(driver);
			
			double cost = 50 - i;
			ride.setTotalCost(cost);
			ride.setTotalLength(100D);
			this.rideRepository.save(ride);
			
			end = end.getHour() > 20 ? end.minusHours(1): end.plusHours(1);

//			ride #2
			Ride ride2 = new Ride();
			ride2.setStatus(Status.FINISHED);
			ride2.setEndTime(end);
			ride2.setDriver(driver);
			
			cost = i;
			ride2.setTotalCost(cost);
			ride2.setTotalLength(100D);
			this.rideRepository.save(ride2);
		}
		LocalDateTime queryEnd = end.minusDays(10);
		
		List<GraphEntryDTO> graphData = rideRepository.getDriverGraphData(queryStart, queryEnd, driver.getId());
		
		assertEquals(10, graphData.size());
		for(GraphEntryDTO entry : graphData) {
			assertEquals(50D, entry.getCostSum());
			assertEquals(200D, entry.getLength());
			assertEquals(2, entry.getNumberOfRides());
		}
	}
	
	
	@Test
	public void getDriverGraphData_non_existant_user() {
		LocalDateTime queryStart = LocalDateTime.of(2009, 11, 16, 0, 0);
		LocalDateTime queryEnd = LocalDateTime.of(2012, 11, 16, 0, 0);

		List<GraphEntryDTO> graphData = rideRepository.getDriverGraphData(queryStart, queryEnd, 123123L);

		assertEquals(0, graphData.size());
	}
	
	@Test
	public void getDriverGraphData_happy_half_are_finished() {
		LocalDateTime queryStart = LocalDateTime.of(2009, 11, 16, 0, 0);
		LocalDateTime queryEnd = LocalDateTime.of(2022, 11, 16, 0, 0);
		
		LocalDateTime end = LocalDateTime.of(2011, 11, 16, 0, 0);
		Driver driver = getDummyDriver();
		
//		sums for the day complement eachother to be 50 on all days
//		length is 100 per ride, 2 rides a day => total 200 per day
		for(int i = 0; i < 20; ++i) {
			end = end.plusDays(1);
//			ride #1
			Ride ride = new Ride();
			ride.setStatus(Status.FINISHED);
			ride.setEndTime(end);
			ride.setDriver(driver);
			
			double cost = 50 - i;
			ride.setTotalCost(cost);
			ride.setTotalLength(100D);
			this.rideRepository.save(ride);
			
			end = end.getHour() > 20 ? end.minusHours(1): end.plusHours(1);

//			ride #2
			Ride ride2 = new Ride();
			ride2.setStatus(Status.CANCELED);
			ride2.setEndTime(end);
			ride2.setDriver(driver);
			
			cost = i;
			ride2.setTotalCost(cost);
			ride2.setTotalLength(100D);
			this.rideRepository.save(ride2);
		}
		
		List<GraphEntryDTO> graphData = rideRepository.getDriverGraphData(queryStart, queryEnd, driver.getId());
		
		assertEquals(20, graphData.size());
		for(int i = 0; i < 20; ++i) {
			assertEquals(50D - i, graphData.get(i).getCostSum());
			assertEquals(100D, graphData.get(i).getLength());
			assertEquals(1, graphData.get(i).getNumberOfRides());
		}
	}
	
	
	@Test
	public void getDriverGraphData_passenger_with_no_rides() {
		LocalDateTime queryStart = LocalDateTime.of(2009, 11, 16, 0, 0);
		LocalDateTime queryEnd = LocalDateTime.of(2022, 11, 16, 0, 0);
		
		LocalDateTime end = LocalDateTime.of(2011, 11, 16, 0, 0);
		Passenger driver = getDummyPassenger();
		
		List<GraphEntryDTO> graphData = rideRepository.getDriverGraphData(queryStart, queryEnd, driver.getId());
		
		assertEquals(0, graphData.size());
	}
	
	
	
	
	
	@Test
	public void getDriverGraphData_happy_are_sorted() {
		LocalDateTime queryStart = LocalDateTime.of(2009, 11, 16, 0, 0);
		LocalDateTime queryEnd = LocalDateTime.of(2012, 11, 16, 0, 0);
		
		LocalDateTime end = LocalDateTime.of(2011, 11, 16, 0, 0);
		Driver driver = getDummyDriver();
		
//		sums for the day complement eachother to be 50 on all days
//		length is 100 per ride, 2 rides a day => total 200 per day
		List<Ride> rides = new ArrayList<>();
		for(int i = 0; i < 20; ++i) {
			end = end.plusDays(1);
//			ride #1
			Ride ride = new Ride();
			ride.setStatus(Status.FINISHED);
			ride.setEndTime(end);
			ride.setDriver(driver);
			
			double cost = 50 - i;
			ride.setTotalCost(cost);
			ride.setTotalLength(100D);
			this.rideRepository.save(ride);
			rides.add(ride);
			
			end = end.getHour() > 20 ? end.minusHours(1): end.plusHours(1);

//			ride #2
			Ride ride2 = new Ride();
			ride2.setEndTime(end);
			ride2.setStatus(Status.FINISHED);
			ride2.setDriver(driver);
			
			cost = i;
			ride2.setTotalCost(cost);
			ride2.setTotalLength(100D);
			this.rideRepository.save(ride2);
			rides.add(ride2);
		}
		
		List<GraphEntryDTO> graphData = rideRepository.getDriverGraphData(queryStart, queryEnd, driver.getId());
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		Set<String> datesSet = new TreeSet<>(rides.stream().map(ride -> ride.getEndTime().format(formatter)).toList());
		List<String> dates = new ArrayList<>(datesSet);
		Collections.sort(dates);
		
		assertEquals(20, graphData.size());
		for(int i = 0; i < 20; ++i) {
			assertEquals(dates.get(i), graphData.get(i).getTime());
			assertEquals(50D, graphData.get(i).getCostSum());
			assertEquals(200D, graphData.get(i).getLength());
			assertEquals(2, graphData.get(i).getNumberOfRides());
		}

	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
//	public List<GraphEntryDTO> getOverallGraphData(LocalDateTime start, LocalDateTime end);
	
	
	
	
@Test
public void getAdminGraphData_happy() {
	LocalDateTime queryStart = LocalDateTime.of(2009, 11, 16, 0, 0);
	LocalDateTime queryEnd = LocalDateTime.of(2012, 11, 16, 0, 0);
	
	LocalDateTime end = LocalDateTime.of(2011, 11, 16, 0, 0);
	
//	sums for the day complement eachother to be 50 on all days
//	length is 100 per ride, 2 rides a day => total 200 per day
	for(int i = 0; i < 20; ++i) {
		end = end.plusDays(1);
//		ride #1
		Ride ride = new Ride();
		ride.setStatus(Status.FINISHED);
		ride.setEndTime(end);
		
		double cost = 50 - i;
		ride.setTotalCost(cost);
		ride.setTotalLength(100D);
		this.rideRepository.save(ride);
		
		end = end.getHour() > 20 ? end.minusHours(1): end.plusHours(1);

//		ride #2
		Ride ride2 = new Ride();
		ride2.setEndTime(end);
		ride2.setStatus(Status.FINISHED);
		
		cost = i;
		ride2.setTotalCost(cost);
		ride2.setTotalLength(100D);
		this.rideRepository.save(ride2);
	}
	
	List<GraphEntryDTO> graphData = rideRepository.getOverallGraphData(queryStart, queryEnd);
	
	assertEquals(20, graphData.size());
	for(GraphEntryDTO entry : graphData) {
		assertEquals(50D, entry.getCostSum());
		assertEquals(200D, entry.getLength());
		assertEquals(2, entry.getNumberOfRides());
	}

}

@Test
public void getAdminGraphData_happy_half_interval() {
	LocalDateTime queryStart = LocalDateTime.of(2009, 11, 16, 0, 0);
	
	LocalDateTime end = LocalDateTime.of(2011, 11, 16, 0, 0);
	
//	sums for the day complement eachother to be 50 on all days
//	length is 100 per ride, 2 rides a day => total 200 per day
	for(int i = 0; i < 20; ++i) {
		end = end.plusDays(1);
//		ride #1
		Ride ride = new Ride();
		ride.setStatus(Status.FINISHED);
		ride.setEndTime(end);
		
		double cost = 50 - i;
		ride.setTotalCost(cost);
		ride.setTotalLength(100D);
		this.rideRepository.save(ride);
		
		end = end.getHour() > 20 ? end.minusHours(1): end.plusHours(1);

//		ride #2
		Ride ride2 = new Ride();
		ride2.setStatus(Status.FINISHED);
		ride2.setEndTime(end);
		
		cost = i;
		ride2.setTotalCost(cost);
		ride2.setTotalLength(100D);
		this.rideRepository.save(ride2);
	}
	LocalDateTime queryEnd = end.minusDays(10);
	
	List<GraphEntryDTO> graphData = rideRepository.getOverallGraphData(queryStart, queryEnd);
	
	assertEquals(10, graphData.size());
	for(GraphEntryDTO entry : graphData) {
		assertEquals(50D, entry.getCostSum());
		assertEquals(200D, entry.getLength());
		assertEquals(2, entry.getNumberOfRides());
	}
}


@Test
public void getAdminGraphData_non_existant_user() {
	LocalDateTime queryStart = LocalDateTime.of(2009, 11, 16, 0, 0);
	LocalDateTime queryEnd = LocalDateTime.of(2012, 11, 16, 0, 0);

	List<GraphEntryDTO> graphData = rideRepository.getOverallGraphData(queryStart, queryEnd);

	assertEquals(0, graphData.size());
}

@Test
public void getAdminGraphData_happy_half_are_finished() {
	LocalDateTime queryStart = LocalDateTime.of(2009, 11, 16, 0, 0);
	LocalDateTime queryEnd = LocalDateTime.of(2022, 11, 16, 0, 0);
	
	LocalDateTime end = LocalDateTime.of(2011, 11, 16, 0, 0);
	
//	sums for the day complement eachother to be 50 on all days
//	length is 100 per ride, 2 rides a day => total 200 per day
	for(int i = 0; i < 20; ++i) {
		end = end.plusDays(1);
//		ride #1
		Ride ride = new Ride();
		ride.setStatus(Status.FINISHED);
		ride.setEndTime(end);
		
		double cost = 50 - i;
		ride.setTotalCost(cost);
		ride.setTotalLength(100D);
		this.rideRepository.save(ride);
		
		end = end.getHour() > 20 ? end.minusHours(1): end.plusHours(1);

//		ride #2
		Ride ride2 = new Ride();
		ride2.setStatus(Status.CANCELED);
		ride2.setEndTime(end);
		
		cost = i;
		ride2.setTotalCost(cost);
		ride2.setTotalLength(100D);
		this.rideRepository.save(ride2);
	}
	
	List<GraphEntryDTO> graphData = rideRepository.getOverallGraphData(queryStart, queryEnd);
	
	assertEquals(20, graphData.size());
	for(int i = 0; i < 20; ++i) {
		assertEquals(50D - i, graphData.get(i).getCostSum());
		assertEquals(100D, graphData.get(i).getLength());
		assertEquals(1, graphData.get(i).getNumberOfRides());
	}
}


@Test
public void getAdminGraphData_passenger_with_no_rides() {
	LocalDateTime queryStart = LocalDateTime.of(2009, 11, 16, 0, 0);
	LocalDateTime queryEnd = LocalDateTime.of(2022, 11, 16, 0, 0);
	
	LocalDateTime end = LocalDateTime.of(2011, 11, 16, 0, 0);
	
	List<GraphEntryDTO> graphData = rideRepository.getOverallGraphData(queryStart, queryEnd);
	
	assertEquals(0, graphData.size());
}



@Test
public void getAdminGraphData_happy_are_sorted() {
	LocalDateTime queryStart = LocalDateTime.of(2009, 11, 16, 0, 0);
	LocalDateTime queryEnd = LocalDateTime.of(2012, 11, 16, 0, 0);
	
	LocalDateTime end = LocalDateTime.of(2011, 11, 16, 0, 0);
	
//	sums for the day complement eachother to be 50 on all days
//	length is 100 per ride, 2 rides a day => total 200 per day
	List<Ride> rides = new ArrayList<>();
	for(int i = 0; i < 20; ++i) {
		end = end.plusDays(1);
//		ride #1
		Ride ride = new Ride();
		ride.setStatus(Status.FINISHED);
		ride.setEndTime(end);
		
		double cost = 50 - i;
		ride.setTotalCost(cost);
		ride.setTotalLength(100D);
		this.rideRepository.save(ride);
		rides.add(ride);
		
		end = end.getHour() > 20 ? end.minusHours(1): end.plusHours(1);

//		ride #2
		Ride ride2 = new Ride();
		ride2.setEndTime(end);
		ride2.setStatus(Status.FINISHED);
		
		cost = i;
		ride2.setTotalCost(cost);
		ride2.setTotalLength(100D);
		this.rideRepository.save(ride2);
		rides.add(ride2);
	}
	
	List<GraphEntryDTO> graphData = rideRepository.getOverallGraphData(queryStart, queryEnd);
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	Set<String> datesSet = new TreeSet<>(rides.stream().map(ride -> ride.getEndTime().format(formatter)).toList());
	List<String> dates = new ArrayList<>(datesSet);
	Collections.sort(dates);
	
	assertEquals(20, graphData.size());
	for(int i = 0; i < 20; ++i) {
		assertEquals(dates.get(i), graphData.get(i).getTime());
		assertEquals(50D, graphData.get(i).getCostSum());
		assertEquals(200D, graphData.get(i).getLength());
		assertEquals(2, graphData.get(i).getNumberOfRides());
	}

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
