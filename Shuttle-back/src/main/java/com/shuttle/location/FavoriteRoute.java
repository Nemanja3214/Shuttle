package com.shuttle.location;

import java.time.LocalDateTime;
import java.util.List;

import com.shuttle.passenger.Passenger;
import com.shuttle.vehicle.vehicleType.VehicleType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="FavoriteRoute")
public class FavoriteRoute {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
    private String favoriteName;
    
	@ManyToMany(fetch = FetchType.EAGER,
			cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Location> locations;
	
	@ManyToMany(fetch = FetchType.EAGER,
			cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Passenger> passengers;
	
	@ManyToOne
    private VehicleType vehicleType;
	
    private Boolean babyTransport;
    private Boolean petTransport;
    private LocalDateTime scheduledTime;
}
