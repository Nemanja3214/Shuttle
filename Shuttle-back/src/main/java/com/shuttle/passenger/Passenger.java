package com.shuttle.passenger;

import java.util.Set;

import com.shuttle.location.Route;
import com.shuttle.user.GenericUser;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Passenger extends GenericUser {
    @OneToMany
    private Set<Route> favoriteRoutes;
    Double finance;
    boolean currentlyRiding;
    boolean Blocked;
}
