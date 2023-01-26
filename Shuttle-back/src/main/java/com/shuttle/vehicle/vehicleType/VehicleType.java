package com.shuttle.vehicle.vehicleType;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jdk.jfr.Enabled;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "vehicle_type")
public class VehicleType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    private String name;
    private double pricePerKM;
}
