package com.shuttle.ride;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import com.shuttle.driver.Driver;
import com.shuttle.location.Location;
import com.shuttle.passenger.Passenger;
import com.shuttle.ride.cancellation.Cancellation;
import com.shuttle.vehicle.vehicleType.VehicleType;
import com.shuttle.location.Route;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
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
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Passenger> passengers;
    @OneToOne(cascade = CascadeType.ALL)
    private Route route;
    private Integer estimatedTimeInMinutes;
    private Boolean babyTransport;
    private Boolean petTransport;
    @ManyToOne
    private VehicleType vehicleType;
    @OneToOne
    private Cancellation rejection;
    private Status status;
    private LocalDateTime scheduledTime; // Can be null.
    private Double totalLength;

    public enum Status {
        PENDING, ACCEPTED, STARTED, REJECTED, CANCELED, FINISHED
    }

    public List<Location> getLocations() {
    	return this.route.getLocations();
    }
    
    public LocalDateTime getEstimatedEndTime() {
    	if (estimatedTimeInMinutes != null && startTime != null) {
    		return startTime.plusMinutes(estimatedTimeInMinutes.longValue());
    	}
    	return null;
    }
}
