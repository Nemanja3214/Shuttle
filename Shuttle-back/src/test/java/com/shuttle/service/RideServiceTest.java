package com.shuttle.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.shuttle.driver.Driver;
import com.shuttle.driver.IDriverRepository;
import com.shuttle.driver.IDriverService;
import com.shuttle.location.IFavouriteRouteRepository;
import com.shuttle.location.ILocationRepository;
import com.shuttle.location.Location;
import com.shuttle.location.dto.LocationDTO;
import com.shuttle.location.dto.RouteDTO;
import com.shuttle.passenger.IPassengerRepository;
import com.shuttle.ride.*;
import com.shuttle.ride.dto.CreateRideDTO;
import com.shuttle.user.dto.BasicUserInfoDTO;
import com.shuttle.vehicle.IVehicleService;
import com.shuttle.vehicle.Vehicle;
import com.shuttle.vehicle.vehicleType.IVehicleTypeRepository;
import com.shuttle.vehicle.vehicleType.VehicleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

@ExtendWith(MockitoExtension.class)
public class RideServiceTest {
    @Autowired
    @InjectMocks
    private RideService rideService;

    @Mock
    private IRideRepository rideRepository;
    @Mock
    private IDriverRepository driverRepository; // TODO: Remove, we have driverService now.
    @Mock
    private IDriverService driverService;
    @Mock
    private IVehicleService vehicleService;
    @Mock
    private IPassengerRepository passengerRepository;
    @Mock
    private IFavouriteRouteRepository favouriteRouteRepository;
    @Mock
    private IVehicleTypeRepository vehicleTypeRepository;
    @Mock
    private ILocationRepository locationRepository;

    @Test
    @DisplayName("findById [positive]")
    public void shouldFindById() {
        final Long id = 123L;
        Ride ride = new Ride();
        ride.setId(id);

        Mockito.when(rideRepository.findById(id)).thenReturn(Optional.of(ride));
        Ride rideGot = rideService.findById(id);

        assertEquals(rideGot.getId(), ride.getId());
    }

    @ParameterizedTest
    @DisplayName("findById [negative] (returns null)")
    @ValueSource(longs = {-1, 0, -32832, 328372})
    public void shouldFindById(Long id) {
        Mockito.when(rideRepository.findById(id)).thenReturn(Optional.empty());
        Ride rideGot = rideService.findById(id);

        assertEquals(rideGot, null);
    }

    @ParameterizedTest
    @DisplayName("shouldScheduleRide [positive] (returns driver)")
    @ArgumentsSource(ShouldCreateDriverArgumentProvider.class)
    public void shouldScheduleRideAvailableDrivers(CreateRideDTO createRideDTO, boolean forFuture, boolean multipleDrivers,
                                                   boolean closerPending,boolean closerAccepted,boolean closerStarted,
                                                   boolean furtherPending,boolean furtherAccepted,boolean furtherStarted) throws NoAvailableDriverException {
        Driver d1 = new Driver();
        d1.setId(1L);
        d1.setName("John");
        d1.setSurname("Smith");
        d1.setAddress("Rumenicka");
        d1.setProfilePicture("asdasd");
        d1.setTelephoneNumber("06012345");
        d1.setEmail("john@gmail.com");
        d1.setPassword("password");
        d1.setAvailable(true);
        d1.setTimeWorkedToday(4L);

        VehicleType vt = new VehicleType(1L, createRideDTO.getVehicleType(), 100);

        Vehicle vehicle1 = new Vehicle();
        vehicle1.setModel("Golf 7");
        vehicle1.setDriver(d1);
        vehicle1.setVehicleType(vt);
        vehicle1.setBabyTransport(createRideDTO.getBabyTransport());
        vehicle1.setPetTransport(createRideDTO.getPetTransport());
        vehicle1.setCurrentLocation(new Location(1L, "negde", 45.1, 19.0));
        vehicle1.setPassengerSeats(createRideDTO.getPassengers().size() + 1);

        Driver d2 = new Driver();
        d2.setId(2L);
        d2.setName("Smith");
        d2.setSurname("Smith");
        d2.setAddress("Rumenicka2");
        d2.setProfilePicture("asdasd");
        d2.setTelephoneNumber("06012345");
        d2.setEmail("john123@gmail.com");
        d2.setPassword("password");


        Vehicle vehicle2 = new Vehicle();
        vehicle2.setModel("Golf 6");
        vehicle2.setDriver(d1);
        vehicle2.setVehicleType(vt);
        vehicle2.setBabyTransport(createRideDTO.getBabyTransport());
        vehicle2.setPetTransport(createRideDTO.getPetTransport());
        vehicle2.setCurrentLocation(new Location(2L, "negde", 55.1, 19.0));
        vehicle2.setPassengerSeats(createRideDTO.getPassengers().size());


        List<Driver> drivers = new ArrayList<>();
        drivers.add(d1);

        Mockito.when(driverRepository.findAllActive()).thenReturn(drivers);
        setMocksHappyPath(createRideDTO, d1, vt, vehicle1, drivers,closerAccepted,closerStarted,closerPending);

        if (multipleDrivers) {
            drivers.add(d2);
            setMocksHappyPath(createRideDTO, d2, vt, vehicle1, drivers,furtherAccepted,furtherStarted,furtherPending);
        }

        Driver result = rideService.findMostSuitableDriver(createRideDTO, forFuture);
        assertEquals(d1, result);

    }

    private void setMocksHappyPath(CreateRideDTO createRideDTO, Driver d1, VehicleType vt, Vehicle vehicle1, List<Driver> drivers,
    boolean accepted,boolean started,boolean pending) {


        Mockito.when(vehicleService.findVehicleTypeByName(createRideDTO.getVehicleType())).thenReturn(Optional.of(vt));
        setRideRepositoryStatus(d1,Ride.Status.PENDING,pending);
        setRideRepositoryStatus(d1,Ride.Status.ACCEPTED,accepted);
        setRideRepositoryStatus(d1,Ride.Status.STARTED,started);
        if (!pending) {
            Mockito.when(driverService.workedMoreThan8Hours(d1)).thenReturn(false);
            Mockito.when(vehicleService.findByDriver(d1)).thenReturn(vehicle1);
        }
    }

    private void setRideRepositoryStatus(Driver d,Ride.Status status,boolean contains){
        List<Ride> rides = new ArrayList<>();
        if (contains){
            rides.add(new Ride());
        }
        Mockito.when(rideRepository.findByDriverAndStatus(d, status)).thenReturn(rides);
    }

}
