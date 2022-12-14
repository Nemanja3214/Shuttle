package com.shuttle.ride.dto;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.shuttle.location.Location;
import com.shuttle.location.dto.LocationDTO;
import com.shuttle.location.RouteDTO;
import com.shuttle.panic.Cancellation;
import com.shuttle.ride.Ride;
import com.shuttle.vehicle.VehicleType;
import lombok.Data;

@Data
public class RideDTO {
	private Long id;
	private List<RouteDTO> locations;
	private String startTime;
	private String endTime;
	private Integer totalCost;
	private RideDriverDTO driver;
	private List<RidePassengerDTO> passengers;
	private Integer estimatedTimeInMinutes;
	private Boolean babyTransport;
	private Boolean petTransport;
	private VehicleType vehicleType;
	private CancellationDTO rejection;
	private Ride.Status status;

	public RideDTO(Ride ride) {
		this.id = ride.getId();
		this.locations = new ArrayList<RouteDTO>();

		List<Location> ls = ride.getLocations();
		for (int i = 0; i < ls.size(); i += 2) {
			RouteDTO d = new RouteDTO(new LocationDTO(ls.get(i)), new LocationDTO(ls.get(i + 1)));
			locations.add(d);
		}

		this.startTime = ride.getStartTime().format(DateTimeFormatter.ISO_DATE_TIME);
		this.endTime = ride.getEndTime().format(DateTimeFormatter.ISO_DATE_TIME);
		this.totalCost = ride.getTotalCost();
		this.driver = new RideDriverDTO(ride.getDriver());
		this.passengers = ride.getPassengers().stream().map(p -> new RidePassengerDTO(p)).toList();
		this.estimatedTimeInMinutes = ride.getEstimatedTimeInMinutes();
		this.babyTransport = ride.getBabyTransport();
		this.petTransport = ride.getPetTransport();
		this.vehicleType = ride.getVehicle().getVehicleType();
		this.rejection = new CancellationDTO(new Cancellation());
		this.status = ride.getStatus();
	}

	public RideDTO() {
		super();
	}

//	public static RideDTO getMock() {
//		Ride r = new Ride();
//		r.setDriver(new Driver());
//
//		r.setId(Long.valueOf(43798));
//
//		r.getDriver().setId(Long.valueOf(0));
//		r.getDriver().setAddress("ASBABS");
//		r.getDriver().getCredentials().setEmail("haksjah");
//		r.getDriver().setName("SJKAHS");
//		r.getDriver().setSurname("ahsjka");
//		r.getDriver().getCredentials().setPassword("hdjk");
//		r.getDriver().setTelephoneNumber("hdkwdhswkjdhsjk");
//		r.getDriver().setProfilePicture("hjksfhfkrjefyewiuf4yur983hf==");
//
//		r.setPassengers(new HashSet<>());
//
//		Passenger p = new Passenger();
//		p.setId(Long.valueOf(0));
//		p.setAddress("ASBABS");
//		p.getCredentials().setEmail("haksjah");
//		p.setName("SJKAHS");
//		p.setSurname("ahsjka");
//		p.getCredentials().setPassword("hdjk");
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
//		r.getRoute().setLocations(new HashSet<>());
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
//		r.setStatus(Ride.Status.Pending);
//
//		return new RideDTO(r);
//	}
}
