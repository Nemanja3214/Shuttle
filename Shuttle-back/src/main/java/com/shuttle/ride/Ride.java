package com.shuttle.ride;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.shuttle.driver.Driver;
import com.shuttle.location.Location;
import com.shuttle.passenger.Passenger;
import com.shuttle.location.Route;
import com.shuttle.vehicle.Vehicle;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer totalCost;
    @OneToOne
    private Driver driver;

    @OneToMany
    private Set<Passenger> passengers;
    @ManyToOne
    private Route route;
    private Integer estimatedTimeInMinutes;
    private Boolean babyTransport;
    private Boolean petTransport;
    @ManyToOne
    private Vehicle vehicle;
    private Status status;

    public enum Status {
        Pending, Accepted, Rejected, Active, Finished
    }

    public List<Location> getLocations() {
        return this.route.getLocations();
    }


}
