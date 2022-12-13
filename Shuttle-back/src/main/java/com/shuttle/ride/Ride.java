package com.shuttle.ride;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.shuttle.common.Entity;
import com.shuttle.driver.Driver;
import com.shuttle.location.Location;
import com.shuttle.passenger.Passenger;
import com.shuttle.location.Route;
import com.shuttle.vehicle.Vehicle;
import com.shuttle.vehicle.VehicleType;
import jakarta.persistence.Embedded;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@jakarta.persistence.Entity
public class Ride extends Entity {
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

    public List<Location> getLocations(){
        return this.route.getLocations();
    }


}
