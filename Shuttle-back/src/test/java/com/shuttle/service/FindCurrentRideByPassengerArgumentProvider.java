package com.shuttle.service;

import com.shuttle.ride.Ride;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class FindCurrentRideByPassengerArgumentProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
        List<List<Ride>> listList = new ArrayList<>();
        Ride started = new Ride();
        started.setStatus(Ride.Status.STARTED);
        Ride accepted = new Ride();
        accepted.setStatus(Ride.Status.ACCEPTED);
        Ride finished = new Ride();
        finished.setStatus(Ride.Status.ACCEPTED);
        addToList(listList, started);
        addToList(listList, finished);
        addToList(listList, accepted);

        add2ToList(listList,started,finished);
        add2ToList(listList,started,accepted);
        add2ToList(listList,accepted,finished);

        List<Ride> list = new ArrayList<>();
        list.add(started);
        list.add(finished);
        list.add(accepted);
        listList.add(list);
        List<Arguments> argumentsList = new ArrayList<>();
        listList.forEach(value ->{
            argumentsList.add(Arguments.of(value));
        });

        return argumentsList.stream();
    }

    private static void addToList(List<List<Ride>> listList, Ride started) {
        List<Ride> list = new ArrayList<>();
        list.add(started);
        listList.add(list);
    }
    private static void add2ToList(List<List<Ride>> listList, Ride first,Ride second) {
        List<Ride> list = new ArrayList<>();
        list.add(first);
        list.add(second);
        listList.add(list);
    }
}
