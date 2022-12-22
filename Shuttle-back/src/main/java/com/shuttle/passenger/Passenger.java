package com.shuttle.passenger;

import java.util.Set;

import com.shuttle.location.Route;
import com.shuttle.user.GenericUser;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="passenger")
public class Passenger extends GenericUser {
    @OneToMany(fetch = FetchType.EAGER)
    private Set<Route> favoriteRoutes;
    Double finance;
    Boolean currentlyRiding;
    Boolean blocked;
}
