package com.shuttle.ride;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.shuttle.driver.Driver;
import com.shuttle.location.Location;
import com.shuttle.passenger.Passenger;
import com.shuttle.location.Route;
import com.shuttle.vehicle.Vehicle;
import com.shuttle.vehicle.VehicleType;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="ride")
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double totalCost;
    @OneToOne
    private Driver driver;
    @OneToMany
    private Set<Passenger> passengers;
    @OneToOne
    private Route route;
    private Integer estimatedTimeInMinutes;
    private Boolean babyTransport;
    private Boolean petTransport;
    @ManyToOne
    private VehicleType vehicleType;
    private Status status;

    public enum Status {
        Pending, Accepted, Rejected, Active, Finished
    }

    public List<Location> getLocations() {
    	return this.route.getLocations();
    }
}
