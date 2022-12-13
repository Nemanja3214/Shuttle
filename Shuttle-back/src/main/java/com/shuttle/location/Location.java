package com.shuttle.location;

import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class Location extends com.shuttle.common.Entity {
    private String address;
    private Double latitude;
    private Double longitude;
}
