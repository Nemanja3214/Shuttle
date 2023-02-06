package com.shuttle.service;

import com.shuttle.driver.Driver;
import com.shuttle.location.dto.LocationDTO;
import com.shuttle.location.dto.RouteDTO;
import com.shuttle.ride.dto.CreateRideDTO;
import com.shuttle.user.dto.BasicUserInfoDTO;
import com.shuttle.vehicle.Vehicle;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ShouldCreateDriverArgumentProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
        List<BasicUserInfoDTO> passengers = new ArrayList<>();
        passengers.add(new BasicUserInfoDTO(2, "john@gmail.com"));
        List<RouteDTO> route = new ArrayList<>();
        LocationDTO locationDTO = new LocationDTO("Bulevar oslobodjenja 46", 45.267136, 19.833549);
        route.add(new RouteDTO(locationDTO, locationDTO));
        List<List<CreateRideDTO>> notScheduled = new ArrayList<>();
        List<List<CreateRideDTO>> scheduled = new ArrayList<>();

        notScheduled.add(getNoScheduleRideDtos(passengers, route, "STANDARD"));
        notScheduled.add(getNoScheduleRideDtos(passengers, route, "VAN"));
        notScheduled.add(getNoScheduleRideDtos(passengers, route, "LUXURY"));
        notScheduled.add(getScheduleRideDtos(passengers, route, "STANDARD"));
        notScheduled.add(getScheduleRideDtos(passengers, route, "VAN"));
        notScheduled.add(getScheduleRideDtos(passengers, route, "LUXURY"));
        passengers.add(new BasicUserInfoDTO(3, "troy@gmail.com"));
        passengers.add(new BasicUserInfoDTO(11, "p1@mgail.com"));
        passengers.add(new BasicUserInfoDTO(12, "p2@mgail.com"));
        scheduled.add(getNoScheduleRideDtos(passengers, route, "STANDARD"));
        scheduled.add(getNoScheduleRideDtos(passengers, route, "VAN"));
        scheduled.add(getNoScheduleRideDtos(passengers, route, "LUXURY"));
        scheduled.add(getScheduleRideDtos(passengers, route, "STANDARD"));
        scheduled.add(getScheduleRideDtos(passengers, route, "VAN"));
        scheduled.add(getScheduleRideDtos(passengers, route, "LUXURY"));
        List<Arguments> argumentsList = new ArrayList<>();

        notScheduled.forEach(value -> {
            value.forEach(value1 -> {
                argumentsList.add(Arguments.of(value1, false, false, false, false, false, false, false, false));
                argumentsList.add(Arguments.of(value1, false, true, false, true, false, false, true, false));
                argumentsList.add(Arguments.of(value1, false, true, false, false, false, false, false, false));
                argumentsList.add(Arguments.of(value1, false, true, true, false, false, false, false, false));
                argumentsList.add(Arguments.of(value1, false, true, false, true, false, false, false, false));
                argumentsList.add(Arguments.of(value1, false, true, false, false, true, false, false, false));
                argumentsList.add(Arguments.of(value1, false, true, false, false, false, true, false, false));
                argumentsList.add(Arguments.of(value1, false, true, false, false, false, false, true, false));
                argumentsList.add(Arguments.of(value1, false, true, false, false, false, false, false, true));

            });
        });
        scheduled.forEach(value -> {
            value.forEach(value1 -> {
                argumentsList.add(Arguments.of(value1, true, false, false, false, false, false, false, false));
                argumentsList.add(Arguments.of(value1, true, true, false, true, false, false, true, false));
                argumentsList.add(Arguments.of(value1, true, true, false, false, false, false, false, false));
                argumentsList.add(Arguments.of(value1, true, true, true, false, false, false, false, false));
                argumentsList.add(Arguments.of(value1, true, true, false, true, false, false, false, false));
                argumentsList.add(Arguments.of(value1, true, true, false, false, true, false, false, false));
                argumentsList.add(Arguments.of(value1, true, true, false, false, false, true, false, false));
                argumentsList.add(Arguments.of(value1, true, true, false, false, false, false, true, false));
                argumentsList.add(Arguments.of(value1, true, true, false, false, false, false, false, true));
            });

        });
        return argumentsList.stream();
    }

    private static List<CreateRideDTO> getNoScheduleRideDtos(List<BasicUserInfoDTO> passengers, List<RouteDTO> route, String type) {
        CreateRideDTO createSinglePassengerStandardRideNoBabyNoPetNoScheduledDTO = new CreateRideDTO(
                passengers,
                route,
                type,
                false,
                false,
                "",
                200.0
        );
        CreateRideDTO createSinglePassengerStandardRideBabyNoPetNoScheduledDTO = new CreateRideDTO(
                passengers,
                route,
                type,
                true,
                false,
                "",
                200.0
        );

        CreateRideDTO createSinglePassengerStandardRideNoBabyPetNoScheduledDTO = new CreateRideDTO(
                passengers,
                route,
                type,
                true,
                false,
                "",
                200.0
        );

        CreateRideDTO createSinglePassengerStandardRideBabyPetNoScheduledDTO = new CreateRideDTO(
                passengers,
                route,
                type,
                true,
                true,
                "",
                200.0
        );
        List<CreateRideDTO> list = new ArrayList<>();
        list.add(createSinglePassengerStandardRideNoBabyNoPetNoScheduledDTO);
        list.add(createSinglePassengerStandardRideNoBabyPetNoScheduledDTO);
        list.add(createSinglePassengerStandardRideBabyNoPetNoScheduledDTO);
        list.add(createSinglePassengerStandardRideBabyPetNoScheduledDTO);
        return list;
    }

    private static List<CreateRideDTO> getScheduleRideDtos(List<BasicUserInfoDTO> passengers, List<RouteDTO> route, String type) {
        CreateRideDTO createSinglePassengerStandardRideNoBabyNoPetScheduledDTO = new CreateRideDTO(
                passengers,
                route,
                type,
                false,
                false,
                "2023-02-11T17:45:00Z",
                200.0
        );
        CreateRideDTO createSinglePassengerStandardRideBabyNoPetScheduledDTO = new CreateRideDTO(
                passengers,
                route,
                type,
                true,
                false,
                "2023-02-11T17:45:00Z",
                200.0
        );

        CreateRideDTO createSinglePassengerStandardRideNoBabyPetScheduledDTO = new CreateRideDTO(
                passengers,
                route,
                type,
                true,
                false,
                "2023-02-11T17:45:00Z",
                200.0
        );

        CreateRideDTO createSinglePassengerStandardRideBabyPetScheduledDTO = new CreateRideDTO(
                passengers,
                route,
                type,
                true,
                true,
                "2023-02-11T17:45:00Z",
                200.0
        );
        List<CreateRideDTO> list = new ArrayList<>();
        list.add(createSinglePassengerStandardRideNoBabyNoPetScheduledDTO);
        list.add(createSinglePassengerStandardRideNoBabyPetScheduledDTO);
        list.add(createSinglePassengerStandardRideBabyNoPetScheduledDTO);
        list.add(createSinglePassengerStandardRideBabyPetScheduledDTO);
        return list;
    }

}
