package com.shuttle.driver;


import java.sql.Timestamp;
import java.util.List;

import com.shuttle.location.Location;
import com.shuttle.note.Note;
import com.shuttle.security.Role;
import com.shuttle.user.GenericUser;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="driver")
public class Driver extends GenericUser {
    boolean available;
    Long timeWorkedToday;
    boolean blocked;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "current_location_id", referencedColumnName = "id")
    Location currentLocation;
    
//    TODO: remove, simulation constructor
    @Builder
    public Driver(String name, String surname, String profilePicture, String telephoneNumber, String address, String email, String password,
    		boolean available, Long timeWorkedToday, boolean blocked, Location currLocation) {
    	this.setName(name);
    	this.setSurname(surname);
    	this.setProfilePicture(profilePicture);
    	this.setTelephoneNumber(telephoneNumber);
    	this.setAddress(address);
    	this.setEmail(email);
    	this.setPassword(password);
    	this.available = available;
    	this.timeWorkedToday = timeWorkedToday;
    	this.blocked = blocked;
    	this.currentLocation = currLocation;
    	
    }

    
}
