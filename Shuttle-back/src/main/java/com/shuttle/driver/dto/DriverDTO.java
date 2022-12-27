package com.shuttle.driver.dto;


import com.shuttle.driver.Driver;
import com.shuttle.location.Location;
import com.shuttle.location.dto.LocationDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverDTO {
    Long id;
    private String name;
    private String surname;
    private String profilePicture;
    private String telephoneNumber;
    private String address;
    private String email;
    private String password;
    
    public Driver to() {
    	Driver d = new Driver();
    	d.setId(id);
    	d.setName(name);
    	d.setSurname(surname);
    	d.setAddress(address);
    	d.setProfilePicture(profilePicture);
    	d.setTelephoneNumber(telephoneNumber);
    	d.setEmail(email);
		d.setPassword(password);
		return d;
    }
    
    public static DriverDTO from(Driver driver) {
    	return new DriverDTO(
    			driver.getId(),
    			driver.getName(),
    			driver.getSurname(),
    			driver.getProfilePicture(),
    			driver.getTelephoneNumber(),
    			driver.getAddress(),
    			driver.getEmail(),
    			driver.getPassword()
    	);
    }
}
