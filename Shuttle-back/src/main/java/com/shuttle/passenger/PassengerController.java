package com.shuttle.passenger;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.shuttle.driver.Driver;
import com.shuttle.location.Location;
import com.shuttle.location.dto.LocationDTO;
import com.shuttle.location.RouteDTO;
import com.shuttle.ride.Ride;
import com.shuttle.ride.dto.RidePageDTO;
import com.shuttle.location.Route;
import com.shuttle.vehicle.VehicleType;

import jakarta.websocket.server.PathParam;

@RestController
public class PassengerController {
	@PostMapping("/api/passenger")
	public ResponseEntity<PassengerDTO> create(@RequestBody Passenger passenger) {
		passenger.setId(Long.valueOf(123));
		return new ResponseEntity<>(new PassengerDTO(passenger), HttpStatus.OK);
	}

	@GetMapping("/api/passenger")
	public ResponseEntity<PassengerPageDTO> getPaginated(@PathParam("page") int page, @PathParam("size") int size) {
		List<Passenger> passengersMock = new ArrayList<>();
//		Passenger p = new Passenger();
//		p.setId(Long.valueOf(0));
//		p.setAddress("ASBABS");
//		p.setEmail("haksjah");
//		p.setName("SJKAHS");
//		p.setSurname("ahsjka");
//		p.setPassword("hdjk");
//		p.setTelephoneNumber("hdkwdhswkjdhsjk");
//		p.setProfilePicture("hjksfhfkrjefyewiuf4yur983hf==");
//		passengersMock.add(p);

		return new ResponseEntity<>(new PassengerPageDTO(passengersMock), HttpStatus.OK);
	}

	@GetMapping("/api/passenger/{id}")
	public ResponseEntity<PassengerDTO> getDetails(@PathVariable("id") Long id) {
		Passenger p = new Passenger();
//		p.setId(Long.valueOf(0));
//		p.setAddress("ASBABS");
//		p.setEmail("haksjah");
//		p.setName("SJKAHS");
//		p.setSurname("ahsjka");
//		p.setPassword("hdjk");
//		p.setTelephoneNumber("hdkwdhswkjdhsjk");
//		p.setProfilePicture("hjksfhfkrjefyewiuf4yur983hf==");

		return new ResponseEntity<>(new PassengerDTO(p), HttpStatus.OK);
	}

	@GetMapping("/api/passenger/activate/{activationId}")
	public ResponseEntity<Void> activate(@PathVariable("activationId") Long activationId) {
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@PutMapping("/api/passenger/{id}")
	public ResponseEntity<PassengerDTO> update(@RequestBody Passenger newData, @PathVariable("id") Long id) {
		Passenger passengerFromDb = new Passenger();
//		passengerFromDb.setId(id);
//
//		if (newData.getName() != null) {
//			passengerFromDb.setName(newData.getName());
//		}
//		if (newData.getSurname() != null) {
//			passengerFromDb.setSurname(newData.getSurname());
//		}
//		if (newData.getEmail() != null) {
//			passengerFromDb.setEmail(newData.getEmail());
//		}
//		if (newData.getPassword() != null) {
//			passengerFromDb.setPassword(newData.getPassword());
//		}
//		if (newData.getAddress() != null) {
//			passengerFromDb.setAddress(newData.getAddress());
//		}
//		if (newData.getTelephoneNumber() != null) {
//			passengerFromDb.setTelephoneNumber(newData.getTelephoneNumber());
//		}
//		if (newData.getProfilePicture() != null) {
//			passengerFromDb.setProfilePicture(newData.getProfilePicture());
//		}

		return new ResponseEntity<PassengerDTO>(new PassengerDTO(passengerFromDb), HttpStatus.OK);
	}

	@GetMapping("/api/passenger/{id}/ride")
	public ResponseEntity<RidePageDTO> getRides(@PathVariable("id") Long passengerId, @PathParam("page") int page,
			@PathParam("size") int size, @PathParam("sort") String sort, @PathParam("from") String from,
			@PathParam("to") String to) {
		List<Ride> ridesMock = new ArrayList<>();
//		Ride r = new Ride();
//		r.setDriver(new Driver());
//
//		r.setId(Long.valueOf(43798));
//
//		r.getDriver().setId(Long.valueOf(0));
//		r.getDriver().setAddress("ASBABS");
//		r.getDriver().setEmail("haksjah");
//		r.getDriver().setName("SJKAHS");
//		r.getDriver().setSurname("ahsjka");
//		r.getDriver().setPassword("hdjk");
//		r.getDriver().setTelephoneNumber("hdkwdhswkjdhsjk");
//		r.getDriver().setProfilePicture("hjksfhfkrjefyewiuf4yur983hf==");
//
//		r.setPassengers(new HashSet<>());
//
//		Passenger p = new Passenger();
//		p.setId(Long.valueOf(0));
//		p.setAddress("ASBABS");
//		p.setEmail("haksjah");
//		p.setName("SJKAHS");
//		p.setSurname("ahsjka");
//		p.setPassword("hdjk");
//		p.setTelephoneNumber("hdkwdhswkjdhsjk");
//		p.setProfilePicture("hjksfhfkrjefyewiuf4yur983hf==");
//		r.getPassengers().add(p);
//
//		Location l = new Location();
//		l.setLatitude(23.32);
//		l.setLongitude(32.23);
//		l.setAddress("hfdkjdfhkdsj");
//
//		// We need two (specifically, an even number) to build RidePageDTO and it's a set so no duplicates.
//		Location l2 = new Location();
//		l2.setLatitude(23.32);
//		l2.setLongitude(542.23);
//		l2.setAddress("ds");
//
//		r.setLocations(new HashSet<>());
//		r.getLocations().add(l);
//		r.getLocations().add(l2);
//
//
//		r.setStartTime(LocalDateTime.now());
//		r.setEndTime(LocalDateTime.now());
//		r.setTotalCost(13902);
//		r.setBabyTransport(false);
//		r.setPetTransport(false);
//		r.setEstimatedTimeInMinutes(12);
//		r.setVehicleType(Type.STANDARD);
//
//		ridesMock.add(r);

		return new ResponseEntity<RidePageDTO>(new RidePageDTO(ridesMock), HttpStatus.OK);
	}
}
