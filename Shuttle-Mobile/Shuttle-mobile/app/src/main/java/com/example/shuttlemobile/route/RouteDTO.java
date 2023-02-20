package com.example.shuttlemobile.route;

import java.io.Serializable;

public class RouteDTO implements Serializable  {
    private LocationDTO departure;
    private LocationDTO destination;

    public RouteDTO() {
    }

    public RouteDTO(LocationDTO departure, LocationDTO destination) {
        this.departure = departure;
        this.destination = destination;
    }

    public LocationDTO getDeparture() {
        return departure;
    }

    public void setDeparture(LocationDTO departure) {
        this.departure = departure;
    }

    public LocationDTO getDestination() {
        return destination;
    }

    public void setDestination(LocationDTO destination) {
        this.destination = destination;
    }

    @Override
    public String toString() {
        return "RouteDTO{" +
                "departure=" + departure +
                ", destination=" + destination +
                '}';
    }
}
