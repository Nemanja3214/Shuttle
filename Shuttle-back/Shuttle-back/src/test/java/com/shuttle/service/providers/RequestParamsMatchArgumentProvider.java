package com.shuttle.service.providers;

import com.shuttle.driver.Driver;
import com.shuttle.location.Location;
import com.shuttle.vehicle.Vehicle;
import com.shuttle.vehicle.vehicleType.VehicleType;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


public class RequestParamsMatchArgumentProvider implements ArgumentsProvider {
    private static final int PASSENGER_COUNT = 4;

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
        VehicleType vt1 = new VehicleType(1L, "STANDARD", 100);
        VehicleType vt2 = new VehicleType(2L, "VAN", 100);
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
        List<Arguments> arguments = new ArrayList<>();
        setParamForVehicle(vt1,vt1, d1, arguments,false,false);
        setParamForVehicle(vt1,vt1, d1, arguments,true,false);
        setParamForVehicle(vt1,vt1, d1, arguments,false,true);
        setParamForVehicle(vt1,vt1, d1, arguments,true,true);

        setParamForVehicle(vt1,vt2, d1, arguments,false,false);
        setParamForVehicle(vt1,vt2, d1, arguments,true,false);
        setParamForVehicle(vt1,vt2, d1, arguments,false,true);
        setParamForVehicle(vt1,vt2, d1, arguments,true,true);
        arguments.add(Arguments.of(d1, false, false, PASSENGER_COUNT, vt1, null));
        return arguments.stream();
    }

    private static void setParamForVehicle(VehicleType vt1,VehicleType vt2, Driver d1, List<Arguments> arguments, boolean baby,boolean pet) {
        Vehicle vehicle1 = new Vehicle();
        vehicle1.setModel("Golf 7");
        vehicle1.setDriver(d1);
        vehicle1.setVehicleType(vt2);
        vehicle1.setBabyTransport(baby);
        vehicle1.setPetTransport(pet);
        vehicle1.setCurrentLocation(new Location(1L, "negde", 45.1, 19.0));
        vehicle1.setPassengerSeats(PASSENGER_COUNT);

        arguments.add(Arguments.of(d1, false, false, PASSENGER_COUNT, vt1, vehicle1));
        arguments.add(Arguments.of(d1, false, true, PASSENGER_COUNT, vt1, vehicle1));
        arguments.add(Arguments.of(d1, true, true, PASSENGER_COUNT, vt1, vehicle1));
        arguments.add(Arguments.of(d1, true, false, PASSENGER_COUNT, vt1, vehicle1));

        arguments.add(Arguments.of(d1, false, false, PASSENGER_COUNT + 1, vt1, vehicle1));
        arguments.add(Arguments.of(d1, false, true, PASSENGER_COUNT + 1, vt1, vehicle1));
        arguments.add(Arguments.of(d1, true, true, PASSENGER_COUNT + 1, vt1, vehicle1));
        arguments.add(Arguments.of(d1, true, false, PASSENGER_COUNT + 1, vt1, vehicle1));

        arguments.add(Arguments.of(d1, false, false, PASSENGER_COUNT - 1, vt1, vehicle1));
        arguments.add(Arguments.of(d1, false, true, PASSENGER_COUNT - 1, vt1, vehicle1));
        arguments.add(Arguments.of(d1, true, true, PASSENGER_COUNT - 1, vt1, vehicle1));
        arguments.add(Arguments.of(d1, true, false, PASSENGER_COUNT - 1, vt1, vehicle1));
    }
}
