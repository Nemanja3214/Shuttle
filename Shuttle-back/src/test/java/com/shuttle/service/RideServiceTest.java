package com.shuttle.service;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.shuttle.common.exception.NonExistantUserException;
import com.shuttle.driver.Driver;
import com.shuttle.driver.IDriverRepository;
import com.shuttle.driver.IDriverService;
import com.shuttle.location.IFavouriteRouteRepository;
import com.shuttle.location.ILocationRepository;
import com.shuttle.location.Location;
import com.shuttle.passenger.IPassengerRepository;
import com.shuttle.passenger.Passenger;
import com.shuttle.ride.*;
import com.shuttle.ride.dto.CreateRideDTO;
import com.shuttle.service.providers.FindCurrentRideByPassengerArgumentProvider;
import com.shuttle.service.providers.RequestParamsMatchArgumentProvider;
import com.shuttle.service.providers.ShouldCreateDriverArgumentProvider;
import com.shuttle.vehicle.IVehicleService;
import com.shuttle.vehicle.Vehicle;
import com.shuttle.vehicle.vehicleType.IVehicleTypeRepository;
import com.shuttle.vehicle.vehicleType.VehicleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    @DisplayName("ScheduleRideNoDriversNoSchedule [negative] (returns null)")
    public void shouldScheduleRideNoScheduleNoDriver() throws NoAvailableDriverException {

        Mockito.when(driverRepository.findAllActive()).thenReturn(new ArrayList<>());
        Driver result = rideService.findMostSuitableDriver(new CreateRideDTO(), true);
        assertNull(result);

    }

    @Test
    @DisplayName("ScheduleRideNoDriversNoSchedule [negative] (returns null)")
    public void shouldScheduleRideScheduleNoDriver() throws NoAvailableDriverException {
        Mockito.when(driverRepository.findAllActive()).thenReturn(new ArrayList<>());
        assertThrows(NoAvailableDriverException.class, () -> {
            Driver result = rideService.findMostSuitableDriver(new CreateRideDTO(), false);
        });

    }

    @ParameterizedTest
    @DisplayName("shouldScheduleRideWorked8hHrs [negative] (returns null)")
    @ArgumentsSource(ShouldCreateDriverArgumentProvider.class)
    public void shouldScheduleRideWorked8hHrs(CreateRideDTO createRideDTO, boolean forFuture, boolean multipleDrivers,
                                              boolean closerPending, boolean closerAccepted, boolean closerStarted,
                                              boolean furtherPending, boolean furtherAccepted, boolean furtherStarted) throws NoAvailableDriverException {
        Driver d1 = getDriver1();

        VehicleType vt = new VehicleType(1L, createRideDTO.getVehicleType(), 100);

        Vehicle vehicle1 = getVehicle1(createRideDTO, d1, vt);
        vehicle1.setPassengerSeats(createRideDTO.getPassengers().size() - 1);
        Driver d2 = getDriver2();

        Vehicle vehicle2 = getVehicle2(createRideDTO, d1, vt);
        vehicle2.setPassengerSeats(createRideDTO.getPassengers().size() - 1);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(d1);


        int estimatedEnd = 20;
        Mockito.when(vehicleService.findVehicleTypeByName(createRideDTO.getVehicleType())).thenReturn(Optional.of(vt));
        setRideRepositoryStatus(d1, Ride.Status.PENDING, closerPending, estimatedEnd);
        setRideRepositoryStatus(d1, Ride.Status.ACCEPTED, closerAccepted, estimatedEnd);
        setRideRepositoryStatus(d1, Ride.Status.STARTED, closerStarted, estimatedEnd);
        if (!closerPending) {
            Mockito.when(driverService.workedMoreThan8Hours(d1)).thenReturn(true);
        }

        if (multipleDrivers) {
            drivers.add(d2);
            Mockito.when(vehicleService.findVehicleTypeByName(createRideDTO.getVehicleType())).thenReturn(Optional.of(vt));
            setRideRepositoryStatus(d2, Ride.Status.PENDING, furtherPending, estimatedEnd);
            setRideRepositoryStatus(d2, Ride.Status.ACCEPTED, furtherAccepted, estimatedEnd);
            setRideRepositoryStatus(d2, Ride.Status.STARTED, closerStarted, estimatedEnd);
            if (!furtherPending) {
                Mockito.when(driverService.workedMoreThan8Hours(d2)).thenReturn(true);
            }
        }
        Mockito.when(driverRepository.findAllActive()).thenReturn(drivers);
        if (forFuture) {
            Driver result = rideService.findMostSuitableDriver(createRideDTO, true);
            assertEquals(null, result);
        } else {
            assertThrows(NoAvailableDriverException.class, () -> {
                rideService.findMostSuitableDriver(createRideDTO, false);
            });
        }


    }


    @ParameterizedTest
    @DisplayName("shouldScheduleRideNoVehicle [negative] (returns null)")
    @ArgumentsSource(ShouldCreateDriverArgumentProvider.class)
    public void shouldScheduleRideNoVehicle(CreateRideDTO createRideDTO, boolean forFuture, boolean multipleDrivers,
                                            boolean closerPending, boolean closerAccepted, boolean closerStarted,
                                            boolean furtherPending, boolean furtherAccepted, boolean furtherStarted) throws NoAvailableDriverException {
        Driver d1 = getDriver1();

        VehicleType vt = new VehicleType(1L, createRideDTO.getVehicleType(), 100);

        Vehicle vehicle1 = getVehicle1(createRideDTO, d1, vt);
        Driver d2 = getDriver2();

        Vehicle vehicle2 = getVehicle2(createRideDTO, d1, vt);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(d1);


        setMocksHappyPath(createRideDTO, d1, vt, vehicle1, drivers, closerAccepted, closerStarted, closerPending, 15);
        if (!closerPending)
            Mockito.when(vehicleService.findByDriver(d1)).thenReturn(null);

        if (multipleDrivers) {
            drivers.add(d2);
            setMocksHappyPath(createRideDTO, d2, vt, vehicle2, drivers, furtherAccepted, furtherStarted, furtherPending, 20);
            if (!furtherPending)
                Mockito.when(vehicleService.findByDriver(d2)).thenReturn(null);
        }
        Mockito.when(driverRepository.findAllActive()).thenReturn(drivers);
        if (forFuture) {
            Driver result = rideService.findMostSuitableDriver(createRideDTO, true);
            assertEquals(null, result);
        } else {
            assertThrows(NoAvailableDriverException.class, () -> {
                rideService.findMostSuitableDriver(createRideDTO, false);
            });
        }


    }


    @ParameterizedTest
    @DisplayName("shouldScheduleRideRequestParamsMisMatch [negative] (returns null)")
    @ArgumentsSource(ShouldCreateDriverArgumentProvider.class)
    public void shouldScheduleRideRequestParamsMisMatch(CreateRideDTO createRideDTO, boolean forFuture, boolean multipleDrivers,
                                                        boolean closerPending, boolean closerAccepted, boolean closerStarted,
                                                        boolean furtherPending, boolean furtherAccepted, boolean furtherStarted) throws NoAvailableDriverException {
        Driver d1 = getDriver1();

        VehicleType vt = new VehicleType(1L, createRideDTO.getVehicleType(), 100);

        Vehicle vehicle1 = getVehicle1(createRideDTO, d1, vt);
        vehicle1.setPassengerSeats(createRideDTO.getPassengers().size() - 1);
        Driver d2 = getDriver2();

        Vehicle vehicle2 = getVehicle2(createRideDTO, d1, vt);
        vehicle2.setPassengerSeats(createRideDTO.getPassengers().size() - 1);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(d1);


        setMocksHappyPath(createRideDTO, d1, vt, vehicle1, drivers, closerAccepted, closerStarted, closerPending, 15);

        if (multipleDrivers) {
            drivers.add(d2);
            setMocksHappyPath(createRideDTO, d2, vt, vehicle2, drivers, furtherAccepted, furtherStarted, furtherPending, 20);
        }
        Mockito.when(driverRepository.findAllActive()).thenReturn(drivers);
        if (forFuture) {
            Driver result = rideService.findMostSuitableDriver(createRideDTO, true);
            assertEquals(null, result);
        } else {
            assertThrows(NoAvailableDriverException.class, () -> {
                rideService.findMostSuitableDriver(createRideDTO, false);
            });
        }


    }

    @ParameterizedTest
    @DisplayName("shouldScheduleRide [negative] (returns null)")
    @ArgumentsSource(ShouldCreateDriverArgumentProvider.class)
    public void shouldScheduleRideVehicleTypeMismatch(CreateRideDTO createRideDTO, boolean forFuture, boolean multipleDrivers,
                                                      boolean closerPending, boolean closerAccepted, boolean closerStarted,
                                                      boolean furtherPending, boolean furtherAccepted, boolean furtherStarted) throws NoAvailableDriverException {
        Driver d1 = getDriver1();

        VehicleType vt = new VehicleType(1L, createRideDTO.getVehicleType(), 100);

        Vehicle vehicle1 = getVehicle1(createRideDTO, d1, vt);

        Driver d2 = getDriver2();

        Vehicle vehicle2 = getVehicle2(createRideDTO, d1, vt);

        vt = new VehicleType(2L, "WE", 100);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(d1);

        Mockito.when(driverRepository.findAllActive()).thenReturn(drivers);
        setMocksHappyPath(createRideDTO, d1, vt, vehicle1, drivers, closerAccepted, closerStarted, closerPending, 15);

        if (multipleDrivers) {
            drivers.add(d2);
            setMocksHappyPath(createRideDTO, d2, vt, vehicle2, drivers, furtherAccepted, furtherStarted, furtherPending, 20);
        }
        if (forFuture) {
            Driver result = rideService.findMostSuitableDriver(createRideDTO, true);
            assertEquals(null, result);
        } else {
            assertThrows(NoAvailableDriverException.class, () -> {
                rideService.findMostSuitableDriver(createRideDTO, false);
            });
        }


    }

    @ParameterizedTest
    @DisplayName("shouldScheduleRide [positive] (returns driver)")
    @ArgumentsSource(ShouldCreateDriverArgumentProvider.class)
    public void shouldScheduleRide(CreateRideDTO createRideDTO, boolean forFuture, boolean multipleDrivers,
                                   boolean closerPending, boolean closerAccepted, boolean closerStarted,
                                   boolean furtherPending, boolean furtherAccepted, boolean furtherStarted) throws NoAvailableDriverException {
        Driver d1 = getDriver1();

        VehicleType vt = new VehicleType(1L, createRideDTO.getVehicleType(), 100);

        Vehicle vehicle1 = getVehicle1(createRideDTO, d1, vt);

        Driver d2 = getDriver2();

        Vehicle vehicle2 = getVehicle2(createRideDTO, d1, vt);


        List<Driver> drivers = new ArrayList<>();
        drivers.add(d1);

        Mockito.when(driverRepository.findAllActive()).thenReturn(drivers);
        setMocksHappyPath(createRideDTO, d1, vt, vehicle1, drivers, closerAccepted, closerStarted, closerPending, 15);

        if (multipleDrivers) {
            drivers.add(d2);
            setMocksHappyPath(createRideDTO, d2, vt, vehicle2, drivers, furtherAccepted, furtherStarted, furtherPending, 20);
        }

        Driver result = rideService.findMostSuitableDriver(createRideDTO, forFuture);
        assertEquals(d1, result);

    }

    @ParameterizedTest
    @DisplayName("checkRequestParamsMatch [positive] (returns boolean)")
    @ArgumentsSource(RequestParamsMatchArgumentProvider.class)
    public void checkParamsChangeMatch(Driver d, boolean baby, boolean pet, int seatsNeeded, VehicleType vehicleType, Vehicle v) {
        Mockito.when(vehicleService.findByDriver(d)).thenReturn(v);
        boolean result = rideService.requestParamsMatch(d, baby, pet, seatsNeeded, vehicleType);
        if (v == null) {
            assertFalse(result);
        } else if (!v.getBabyTransport() && baby) {
            assertFalse(result);
        } else if (!v.getPetTransport() && pet) {
            assertFalse(result);
        } else if (v.getPassengerSeats() < seatsNeeded) {
            assertFalse(result);
        } else if (!v.getVehicleType().equals(vehicleType)) {
            assertFalse(result);
        } else
            assertTrue(result);
    }

    @Test
    @DisplayName("shouldFindCurrentRideByDriverStarted [positive] (returns ride)")
    public void findCurrentRideByDriverStarted() {
        Driver driver = new Driver();
        Ride ride = new Ride();
        List<Ride> rideList = new ArrayList<>();
        rideList.add(ride);
        Mockito.when(rideRepository.findByDriverAndStatus(driver, Ride.Status.STARTED)).thenReturn(rideList);
        assertEquals(ride, rideService.findCurrentRideByDriver(driver));

    }

    @Test
    @DisplayName("shouldFindCurrentRideByDriverAccepted [positive] (returns ride)")
    public void findCurrentRideByDriverAccepted() {
        Driver driver = new Driver();
        Ride ride = new Ride();
        List<Ride> rideList = new ArrayList<>();
        rideList.add(ride);
        Mockito.when(rideRepository.findByDriverAndStatus(driver, Ride.Status.STARTED)).thenReturn(new ArrayList<>());
        Mockito.when(rideRepository.findByDriverAndStatus(driver, Ride.Status.ACCEPTED)).thenReturn(rideList);
        assertEquals(ride, rideService.findCurrentRideByDriver(driver));

    }

    @Test
    @DisplayName("shouldFindCurrentRideByDriverPending [positive] (returns ride)")
    public void findCurrentRideByDriverPending() {
        Driver driver = new Driver();
        Ride ride = new Ride();
        List<Ride> rideList = new ArrayList<>();
        rideList.add(ride);
        Mockito.when(rideRepository.findByDriverAndStatus(driver, Ride.Status.STARTED)).thenReturn(new ArrayList<>());
        Mockito.when(rideRepository.findByDriverAndStatus(driver, Ride.Status.ACCEPTED)).thenReturn(new ArrayList<>());
        Mockito.when(rideRepository.findByDriverAndStatus(driver, Ride.Status.PENDING)).thenReturn(rideList);
        assertEquals(ride, rideService.findCurrentRideByDriver(driver));

    }

    @Test
    @DisplayName("shouldFindCurrentRideByDriverPending [negative] (returns null)")
    public void findCurrentRideByDriver() {
        Driver driver = new Driver();
        Ride ride = new Ride();
        List<Ride> rideList = new ArrayList<>();
        rideList.add(ride);
        Mockito.when(rideRepository.findByDriverAndStatus(driver, Ride.Status.STARTED)).thenReturn(new ArrayList<>());
        Mockito.when(rideRepository.findByDriverAndStatus(driver, Ride.Status.ACCEPTED)).thenReturn(new ArrayList<>());
        Mockito.when(rideRepository.findByDriverAndStatus(driver, Ride.Status.PENDING)).thenReturn(new ArrayList<>());
        assertNull(rideService.findCurrentRideByDriver(driver));

    }

    @Test
    @DisplayName("shouldFindCurrentRideByDriverStartedAcceptedStarted [positive] (returns ride)")
    public void findCurrentRideByDriverStartedAcceptedStarted() {
        Driver driver = new Driver();
        Ride ride = new Ride();
        List<Ride> rideList = new ArrayList<>();
        rideList.add(ride);
        Mockito.when(rideRepository.findByDriverAndStatus(driver, Ride.Status.STARTED)).thenReturn(rideList);
        assertEquals(ride, rideService.findCurrentRideByDriver(driver));

    }

    @Test
    @DisplayName("shouldFindCurrentRideByDriverStartedAcceptedAccepted [positive] (returns ride)")
    public void findCurrentRideByDriverStartedAcceptedAccepted() {
        Driver driver = new Driver();
        Ride ride = new Ride();
        List<Ride> rideList = new ArrayList<>();
        rideList.add(ride);
        Mockito.when(rideRepository.findByDriverAndStatus(driver, Ride.Status.STARTED)).thenReturn(new ArrayList<>());
        Mockito.when(rideRepository.findByDriverAndStatus(driver, Ride.Status.ACCEPTED)).thenReturn(rideList);
        assertEquals(ride, rideService.findCurrentRideByDriver(driver));

    }

    @Test
    @DisplayName("shouldFindCurrentRideByDriverStartedAccepted [negative] (returns null)")
    public void findCurrentRideByDriverStartedAccepted() {
        Driver driver = new Driver();
        Ride ride = new Ride();
        List<Ride> rideList = new ArrayList<>();
        rideList.add(ride);
        Mockito.when(rideRepository.findByDriverAndStatus(driver, Ride.Status.STARTED)).thenReturn(new ArrayList<>());
        Mockito.when(rideRepository.findByDriverAndStatus(driver, Ride.Status.ACCEPTED)).thenReturn(new ArrayList<>());
        assertNull(rideService.findCurrentRideByDriver(driver));

    }

    @ParameterizedTest
    @DisplayName("shouldFindCurrentRideByPassenger [positive] (returns ride)")
    @ArgumentsSource(FindCurrentRideByPassengerArgumentProvider.class)
    public void findCurrentRideByPassenger(List<Ride> rides) {
        Passenger passenger = new Passenger();
        passenger.setId(1L);
        Mockito.when(rideRepository.findStartedAcceptedPendingByPassenger(passenger.getId())).thenReturn(rides);

        if (rides.size() == 1) {
            assertEquals(rides.get(0), rideService.findCurrentRideByPassenger(passenger));
        } else if (rides.size() == 2) {
            int i = 0;
            Ride ride = null;
            for (i = 0; i < rides.size(); i++) {
                if (rides.get(i).getStatus() == Ride.Status.STARTED) {
                    ride = rides.get(i);
                }
            }
            if (ride == null) {
                for (i = 0; i < rides.size(); i++) {
                    if (rides.get(i).getStatus() == Ride.Status.FINISHED) {
                        ride = rides.get(i);
                    }
                }
            }
            if (ride == null) ride = rides.get(0);

            assertEquals(ride, rideService.findCurrentRideByPassenger(passenger));
        } else {
            int i = 0;
            Ride ride = null;
            for (i = 0; i < rides.size(); i++) {
                if (rides.get(i).getStatus() == Ride.Status.STARTED) {
                    ride = rides.get(i);
                }
            }
            if (ride == null) {
                for (i = 0; i < rides.size(); i++) {
                    if (rides.get(i).getStatus() == Ride.Status.FINISHED) {
                        ride = rides.get(i);
                    }
                }
            }
            if (ride == null) ride = rides.get(0);

            assertEquals(ride, rideService.findCurrentRideByPassenger(passenger));
        }

    }

    @Test
    @DisplayName("shouldFindCurrentRideByPassenger [negative] (returns null)")
    @ArgumentsSource(FindCurrentRideByPassengerArgumentProvider.class)
    public void findCurrentRideByPassengerNegative() {
        Passenger passenger = new Passenger();
        passenger.setId(1L);
        Mockito.when(rideRepository.findStartedAcceptedPendingByPassenger(passenger.getId())).thenReturn(new ArrayList<>());
        assertNull(rideService.findCurrentRideByPassenger(passenger));
    }

    @Test
    @DisplayName("shouldFindRidesByPassengerInDateRange [positive] (returns list<ride>)")
    public void findRidesByPassengerInDateRange() throws NonExistantUserException {
        Passenger passenger = new Passenger();
        passenger.setId(1L);
        Mockito.when(this.passengerRepository.findById(passenger.getId())).thenReturn(Optional.of(passenger));
        Pageable pageable = Pageable.unpaged();
        List<Ride> rides = new ArrayList<>();
        rides.add(new Ride());
        ZonedDateTime zdt;

        zdt = ZonedDateTime.parse("2023-01-11T17:45:00Z");
        LocalDateTime fromTime = zdt.toLocalDateTime();

        zdt = ZonedDateTime.parse("2023-01-11T17:45:00Z");
        LocalDateTime toTime = zdt.toLocalDateTime();

        Mockito.when(this.rideRepository.getAllByPassengerAndBetweenDates(fromTime, toTime, passenger, pageable)).thenReturn(rides);

        assertEquals(rides,rideService.findRidesByPassengerInDateRange(passenger.getId(),"2023-01-11T17:45:00Z","2023-01-11T17:45:00Z",pageable));

    }

    @Test
    @DisplayName("shouldFindRidesByPassengerInDateRangeNoUser [negative] (throws NonExistantUserException)")
    public void findRidesByPassengerInDateRangeNoUser() {
        Mockito.when(this.passengerRepository.findById(1L)).thenReturn(Optional.empty());
        Pageable pageable = Pageable.unpaged();
        assertThrows(NonExistantUserException.class,()->{
            rideService.findRidesByPassengerInDateRange(1L,"2023-01-11T17:45:00Z","2023-01-11T17:45:00Z",pageable);
        });
    }

    @Test
    @DisplayName("shouldFindRidesByPassengerInDateRangeBadDate [negative] (throws NonExistantUserException)")
    public void findRidesByPassengerInDateRangeBadDate() {
        Passenger passenger = new Passenger();
        passenger.setId(1L);
        Mockito.when(this.passengerRepository.findById(passenger.getId())).thenReturn(Optional.of(passenger));
        Pageable pageable = Pageable.unpaged();
        assertThrows(DateTimeException.class,()->{
            rideService.findRidesByPassengerInDateRange(passenger.getId(),"123","2023-01-11T17:45:00Z",pageable);
        });
    }


    private static Vehicle getVehicle2(CreateRideDTO createRideDTO, Driver d1, VehicleType vt) {
        Vehicle vehicle2 = new Vehicle();
        vehicle2.setModel("Golf 6");
        vehicle2.setDriver(d1);
        vehicle2.setVehicleType(vt);
        vehicle2.setBabyTransport(createRideDTO.getBabyTransport());
        vehicle2.setPetTransport(createRideDTO.getPetTransport());
        vehicle2.setCurrentLocation(new Location(2L, "negde", 55.1, 19.0));
        vehicle2.setPassengerSeats(createRideDTO.getPassengers().size());
        return vehicle2;
    }

    private static Driver getDriver2() {
        Driver d2 = new Driver();
        d2.setId(2L);
        d2.setName("Smith");
        d2.setSurname("Smith");
        d2.setAddress("Rumenicka2");
        d2.setProfilePicture("asdasd");
        d2.setTelephoneNumber("06012345");
        d2.setEmail("john123@gmail.com");
        d2.setPassword("password");
        return d2;
    }

    private static Vehicle getVehicle1(CreateRideDTO createRideDTO, Driver d1, VehicleType vt) {
        Vehicle vehicle1 = new Vehicle();
        vehicle1.setModel("Golf 7");
        vehicle1.setDriver(d1);
        vehicle1.setVehicleType(vt);
        vehicle1.setBabyTransport(createRideDTO.getBabyTransport());
        vehicle1.setPetTransport(createRideDTO.getPetTransport());
        vehicle1.setCurrentLocation(new Location(1L, "negde", 45.1, 19.0));
        vehicle1.setPassengerSeats(createRideDTO.getPassengers().size() + 1);
        return vehicle1;
    }

    private static Driver getDriver1() {
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
        return d1;
    }


    private void setMocksHappyPath(CreateRideDTO createRideDTO, Driver d1, VehicleType vt, Vehicle vehicle1, List<Driver> drivers,
                                   boolean accepted, boolean started, boolean pending, int estimatedEnd) {


        Mockito.when(vehicleService.findVehicleTypeByName(createRideDTO.getVehicleType())).thenReturn(Optional.of(vt));
        setRideRepositoryStatus(d1, Ride.Status.PENDING, pending, estimatedEnd);
        setRideRepositoryStatus(d1, Ride.Status.ACCEPTED, accepted, estimatedEnd);
        setRideRepositoryStatus(d1, Ride.Status.STARTED, started, estimatedEnd);
        if (!pending) {
            Mockito.when(driverService.workedMoreThan8Hours(d1)).thenReturn(false);
            Mockito.when(vehicleService.findByDriver(d1)).thenReturn(vehicle1);
        }
    }

    private void setRideRepositoryStatus(Driver d, Ride.Status status, boolean contains, int estimatedEnd) {
        List<Ride> rides = new ArrayList<>();
        if (contains) {
            Ride ride = new Ride();
            ride.setEstimatedTimeInMinutes(estimatedEnd);
            ride.setStartTime(LocalDateTime.now());
            rides.add(ride);
        }
        Mockito.when(rideRepository.findByDriverAndStatus(d, status)).thenReturn(rides);
    }


}
