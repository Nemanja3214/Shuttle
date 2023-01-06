package com.shuttle.passenger;

import java.util.Set;

import com.shuttle.location.Route;
import com.shuttle.user.GenericUser;
import com.shuttle.verificationToken.VerificationToken;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Passenger extends GenericUser {
    @OneToMany(fetch = FetchType.EAGER)
    private Set<Route> favoriteRoutes;
    Double finance;
    Boolean currentlyRiding;
    @OneToOne(mappedBy = "passenger", cascade = CascadeType.ALL, orphanRemoval=true)
    private VerificationToken token;
}
